package server_grpc;

import generated.*;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
//import javafx.util.Pair;
//import org.apache.commons.lang3.tuple.Pair;
import org.javatuples.Pair;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.I0Itec.zkclient.IZkChildListener;
import repositories.CityRepository;
import entities.City;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import java.sql.Timestamp;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static java.lang.Math.abs;

import client_grpc.MainGrpcClient;
import utils.RideOfferAlreadyExistsException;
import zkapi.utils.ShardInfo;
import generated.SnapshotInfo;

@Slf4j
public class ServerCommService extends ServerCommGrpc.ServerCommImplBase {
    private static final ConcurrentMap<String, ConcurrentMap<RideOffer, Integer>> ridesVacancies = new ConcurrentHashMap<>();
    @Getter
    private static final ConcurrentMap<String, List<RideOffer>> rides = new ConcurrentHashMap<String, List<RideOffer>>();
    private static ConcurrentMap<RideRequest, Pair<RideOffer, Timestamp>> rideRequests = new ConcurrentHashMap<>();
    private static CityRepository cityRepository = null;
    private final String currCityName;
    public static final int secondsToTimeout = 30;
    public static MainGrpcClient grpcClient;

    public ServerCommService(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
        this.currCityName = ShardInfo.getShardInfo().getCityName();
        this.grpcClient = new MainGrpcClient(ShardInfo.getShardInfo().getCityName());
    }

    private static double calcDist(City P1, City P2, City P0) {
        // calc distance between point P0 and line created py two point P1 and P2
        return abs((P2.getX() - P1.getX()) * (P1.getY() - P0.getY()) - (P1.getX() - P0.getX()) * (P2.getY() - P1.getY())) / sqrt(pow((P2.getX() - P1.getX()), 2) + pow(P2.getY() - P1.getY(), 2));
    }

    private static void cleanTimedoutRequests() {
        // iterate over the current requests in the system and delete those who are timed-out
        Timestamp curr_timestamp = new Timestamp(System.currentTimeMillis());
        for (Map.Entry<RideRequest, Pair<RideOffer, Timestamp>> entry : rideRequests.entrySet()) {
            RideRequest request = entry.getKey();
            RideOffer offer = entry.getValue().getValue0();
            Timestamp timestamp = entry.getValue().getValue1();
            if (timestamp == null){
                continue;
            }
            long milliseconds = curr_timestamp.getTime() - timestamp.getTime();
            int seconds_passed = (int) milliseconds / 1000;
            if (seconds_passed > secondsToTimeout) {
                rideRequests.remove(entry.getKey());
                //rideRequests.entrySet().stream()
                //        .filter(x -> compareRideRequests(x.getKey(), entry.getKey()))
                int currVac = ridesVacancies.get(offer.getDepartureDate()).get(offer);
                ridesVacancies.get(offer.getDepartureDate()).put(offer, currVac + 1);
                log.info("removed request :" + entry.getKey().toString());
            }
        }
    }

    @Override
    public void offerRide(RideOffer newRideOffer, StreamObserver<ServerResponse> responseObserver) {

        try {
            cleanTimedoutRequests();
            if (ShardInfo.getShardInfo().getZkService().isLeader()) {
                offerRideImpl(newRideOffer);
                ServerResponse response = ServerResponse.newBuilder().setResult(ServerResponse.Result.SUCCESS).build();
                responseObserver.onNext(response);
            } else {
                boolean response = grpcClient.offerRide(currCityName, newRideOffer,
                        ShardInfo.getShardInfo().getZkService().getLeaderForCity(currCityName));

                ServerResponse serverResponse = ServerResponse.newBuilder()
                        .setResult(response ? ServerResponse.Result.SUCCESS : ServerResponse.Result.FAILURE)
                        .build();
                responseObserver.onNext(serverResponse);
            }
            responseObserver.onCompleted();
        } catch (RideOfferAlreadyExistsException e) {
            log.info("Ride already found, passing exception to caller");
            responseObserver.onError(new StatusRuntimeException(Status.ALREADY_EXISTS));
        } catch (StatusRuntimeException e) {
//            e.printStackTrace();
            log.info("error happened in offer ride service, passing exception to caller");
            responseObserver.onError(e);
        }

    }

    public void offerRideImpl(RideOffer newRideOffer) throws RideOfferAlreadyExistsException {

        if (getAllRideOffers().contains(newRideOffer)) {
            throw new RideOfferAlreadyExistsException();
        }
        int vacancies = newRideOffer.getVacancies();
        String departureDate = newRideOffer.getDepartureDate();

        // add vacancies
        ConcurrentMap<RideOffer, Integer> currVacanciesOnDate = ridesVacancies.getOrDefault(departureDate, new ConcurrentHashMap<>());
        currVacanciesOnDate.put(newRideOffer, vacancies);
        ridesVacancies.put(departureDate, currVacanciesOnDate);

        // add ride details
        if (!rides.containsKey(departureDate)) {
            rides.put(departureDate, new ArrayList<RideOffer>());
        }
        rides.get(departureDate).add(newRideOffer);
        ShardInfo.getShardInfo().getZkService().broadcastRideOffer(newRideOffer);
    }


    private static void updateVacancies(String date, RideOffer newRideOffer, int diff) {
        ConcurrentMap<RideOffer, Integer> currVacanciesOnDate = ridesVacancies.get(date);
        currVacanciesOnDate.replace(newRideOffer, currVacanciesOnDate.get(newRideOffer) + diff);
        ridesVacancies.replace(date, currVacanciesOnDate);
    }


    public static RideOffer askRideImpl(RideRequest req) {
        String date = req.getDate();
        if (!rides.containsKey(date)) {
            return null;
        }

        Boolean flagFound = false;
        ConcurrentMap<RideOffer, Integer> freePlaces = ridesVacancies.get(date);
        Map<String, City> allCitiesMap = cityRepository.getAllCities();
        for (Map.Entry<RideOffer, Integer> entry : freePlaces.entrySet()) {
            RideOffer possibleRide = entry.getKey();
            int offererVacancies = entry.getValue();

            if (possibleRide.getEndCityName().equals(req.getEndCityName()) && offererVacancies > 0) {
                if (!possibleRide.getStartCityName().equals(req.getStartCityName())) {
                    double dist = calcDist(
                            allCitiesMap.get(possibleRide.getStartCityName()),
                            allCitiesMap.get(possibleRide.getEndCityName()),
                            allCitiesMap.get(req.getStartCityName()));
                    if (dist > possibleRide.getPermittedDeviation()) {
                        continue;
                    }
                }
//                log.info(String.format("vacancies before: %s", ridesVacancies));
                updateVacancies(date, possibleRide, -1);
//                log.info(String.format("vacancies after: %s", ridesVacancies));
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                rideRequests.put(req, new Pair<RideOffer, Timestamp>(possibleRide, timestamp));
                flagFound = true;
//                log.info("Saved space on ride " + possibleRide.toString() + " with remainig spaces: "+offererVacancies );
                return possibleRide;
            }
        }
        return null;
    }

    @Override
    public void askRide(RideRequest req, StreamObserver<RideOffer> responseObserver) {
        try {
            cleanTimedoutRequests();
            if (ShardInfo.getShardInfo().getZkService().isLeader()) {
                RideOffer possibleRide = askRideImpl(req);
                if (possibleRide == null && req.getRecursive()) {
                    possibleRide = askRideFromAllOthers(req);
                }

                if (possibleRide != null) {
                    responseObserver.onNext(possibleRide);
                    responseObserver.onCompleted();
                } else {
                    responseObserver.onError(new StatusRuntimeException(Status.NOT_FOUND));
                }
            } else {
                // if not leader, follow msg to leader and return his answer
                RideOffer response = grpcClient.askRide(currCityName, req,
                        ShardInfo.getShardInfo().getZkService().getLeaderForCity(currCityName));
                if (response != null) {
                    responseObserver.onNext(response);
                    responseObserver.onCompleted();
                }
                else{
                    responseObserver.onError(new StatusRuntimeException(Status.NOT_FOUND));
                }
            }
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        } catch (Exception e) {
            log.info("received unknown exception in ask ride");
            e.printStackTrace();
            responseObserver.onError(e);
        }
    }

    public static RideOffer askRideFromAllOthers(RideRequest req) {
        for (String cityName : cityRepository.getAllCities().keySet().stream()
                .filter(it -> !it.equals(ShardInfo.getShardInfo().getCityName()))
                .toArray(String[]::new)) {
            RideRequest new_req = RideRequest.newBuilder()
                    .setRecursive(false)
                    .setStartCityName(req.getStartCityName())
                    .setEndCityName(req.getEndCityName())
                    .setDate(req.getDate())
                    .setCommit(req.getCommit())
                    .setCancel(req.getCancel())
                    .setRequestTimestamp(req.getRequestTimestamp())
                    .build();
            RideOffer offer = grpcClient.askRide(cityName, new_req);
            if (offer != null) {
                return offer;
            }
        }
        return null;
    }


    @Override
    public void commitOrAbortRideRequest(RideRequest req, StreamObserver<ServerResponse> responseObserver) {
        commitOrAbortRideRequestImpl(req);
        responseObserver.onNext(ServerResponse.newBuilder().setResult(ServerResponse.Result.SUCCESS).build());
        responseObserver.onCompleted();
    }

    public void commitOrAbortRideRequestImpl(RideRequest req) {
        String date = req.getDate();
        if (!rides.containsKey(date)) {
            return;
        }

        RideRequest matchingRideRequest = rideRequests.keySet().stream().filter(it -> (compareRideRequests(it, req))).findFirst().orElseThrow(() -> new StatusRuntimeException(Status.INTERNAL));
        Pair<RideOffer, Timestamp> match = rideRequests.get(matchingRideRequest);

        //rideRequests.remove(req);
        boolean commit = req.getCommit();
        boolean cancel = req.getCancel();

        if (commit && !cancel) {
            // remove timestamp to signal commit
            Pair<RideOffer, Timestamp> new_match = new Pair(match.getValue0(), null);
            rideRequests.replace(matchingRideRequest, new_match);

            SegmentInfo commitedRequst = SegmentInfo.newBuilder()
                                        .setRequest(matchingRideRequest)
                                        .setOffer(match.getValue0()).build();
            ShardInfo.getShardInfo().getZkService().broadcastRideRequest(commitedRequst);
        }

        if (cancel) {
            // else- abort
            RideOffer rideOffer = match.getValue0();
            rideRequests.entrySet().stream()
                    .filter(x -> compareRideRequests(x.getKey(), req))
                    .collect(Collectors.toMap(x -> x.getKey(), x -> x.getValue()));

            // ridesVacancies.get(date).remove(req);
            //rides.remove(req);
            //rideRequests.remove(req);
            updateVacancies(date, rideOffer, 1);
        }
    }



    public static List<RideOffer> getAllRideOffers() {
        return rides.values().stream().flatMap(List::stream).collect(Collectors.toList());
    }

    public static boolean compareRideRequests(RideRequest r1, RideRequest r2) {
        return r1.getDate().equals(r2.getDate()) &&
                r1.getEndCityName().equals(r2.getEndCityName()) &&
//                r1.getStartCityName().equals(r2.getStartCityName()) &&
                r1.getRequestTimestamp().equals(r2.getRequestTimestamp());
    }

    public static boolean compareRideOffers(RideOffer r1, RideOffer r2) {
        return r1.getDepartureDate().equals(r2.getDepartureDate()) &&
                r1.getEndCityName().equals(r2.getEndCityName()) &&
                r1.getStartCityName().equals(r2.getStartCityName()) &&
                r1.getPersonName().equals(r2.getPersonName());
    }


    private int getTakenSpotsInOffer(RideOffer rideOffer){
        return (int) rideRequests.values().stream()
            .filter(x -> compareRideOffers(x.getValue0(), rideOffer) && x.getValue1() == null)
            .count();
    }

    @Override
    public void getSnapshot(Dummy dummy, StreamObserver<SnapshotInfo> responseObserver) {
        try{
            SnapshotInfo snapshotInfo = getSnapshotImpl();
            responseObserver.onNext(snapshotInfo);
            responseObserver.onCompleted();
        }catch (Exception e){
            responseObserver.onError(e);
        }
    }

    public SnapshotInfo getSnapshotImpl(){
        SnapshotInfo.Builder builder = SnapshotInfo.newBuilder();

        // get all current published rides and their vacancies
        List<RideOfferInfo> offersInfo = getAllRideOffers().stream().map(it ->
                RideOfferInfo.newBuilder()
                        .setRideOffer(it)
                        .setTakenSpots(getTakenSpotsInOffer(it))
                        .build())
                .collect(Collectors.toList());
        builder.addAllRideOffers(offersInfo);

        // get all requested segments for this city
        List<SegmentInfo> segmentInfos = rideRequests.keySet().stream().map(
                it -> SegmentInfo.newBuilder()
                        .setIsSatisfied(rideRequests.get(it).getValue1()==null)
                        .setRequest(it)
                        .setOffer(rideRequests.get(it).getValue0()).build()
        ).collect(Collectors.toList());
        builder.addAllRideSegments(segmentInfos);

        SnapshotInfo snapshotInfo = builder.build();
        return snapshotInfo;
    }

    /**
     * this class if for listening to changes in ride offers - meant for the leader to atmoic broadcast to all followers the addition of the ride to the system
     */
    public static class RideOfferListener implements IZkChildListener {

        @Override
        public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
            List<RideOffer> previousRides = getAllRideOffers();
            List<RideOffer> currRides = currentChilds.stream().map(it -> (RideOffer) ShardInfo.getShardInfo().getZkService().getZnodeData(parentPath + "/" + it)).collect(Collectors.toList());
            List<RideOffer> newRides = currRides.stream().filter(it -> !previousRides.contains(it)).collect(Collectors.toList());
            for (RideOffer rideOffer : newRides) {
                String departureDate = rideOffer.getDepartureDate();
                if (!rides.containsKey(departureDate)) {
                    rides.put(departureDate, new ArrayList<RideOffer>());
                }
                rides.get(departureDate).add(rideOffer);
                // add vacancies
                ConcurrentMap<RideOffer, Integer> currVacanciesOnDate = ridesVacancies.getOrDefault(departureDate, new ConcurrentHashMap<>());
                currVacanciesOnDate.put(rideOffer, rideOffer.getVacancies());
                ridesVacancies.put(departureDate, currVacanciesOnDate);
                log.info(ShardInfo.getShardInfo().getHostname() + " added ride bcasted from leader \n" + rideOffer.toString());
            }
        }
    }
    public static class RideRequestListener implements IZkChildListener {

        @Override
        public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
            List<RideOffer> previousRides = getAllRideOffers();
            List<SegmentInfo> currRequests = currentChilds.stream().map(it -> (SegmentInfo) ShardInfo.getShardInfo().getZkService().getZnodeData(parentPath + "/" + it)).collect(Collectors.toList());

            List<SegmentInfo> newRequests = currRequests.stream().filter(it -> !rideRequests.containsKey(it)).collect(Collectors.toList());
            for (SegmentInfo commitedSegment : newRequests) {
                RideRequest commitedRequest = commitedSegment.getRequest();
                RideOffer commitedOffer = commitedSegment.getOffer();
                // save the new request
                Pair<RideOffer, Timestamp> newMatch = new Pair<>(commitedOffer, null);
                rideRequests.put(commitedRequest, newMatch);
                // update ride vacancies
                String departureDate = commitedOffer.getDepartureDate();
                updateVacancies(departureDate, commitedOffer, -1);
                log.info(" added commited request bcasted from leader \n" + commitedSegment.toString());
            }
        }
    }



}
