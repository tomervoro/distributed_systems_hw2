package host.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import client_grpc.MainGrpcClient;
import entities.City;
import generated.*;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import utils.PathPlanningNotFoundException;
import utils.RideOfferAlreadyExistsException;
import zkapi.utils.ShardInfo;
import server_grpc.ServerCommService;
import repositories.CityRepository;
import org.springframework.http.ResponseEntity;
import com.google.protobuf.Timestamp;

import javax.print.attribute.standard.RequestingUserName;

@Slf4j
@RestController
public class RideOfferController {
    private final MainGrpcClient grpcClient;
    private final ServerCommService grpcServer;
    private final CityRepository cityRepository;

    public RideOfferController() {
        this.cityRepository = new CityRepository();
        this.grpcClient = new MainGrpcClient(ShardInfo.getShardInfo().getCityName());
        this.grpcServer = new ServerCommService(cityRepository);
    }


    @GetMapping(value="/snapshot", produces = MediaType.APPLICATION_JSON_VALUE)
     SystemSnapshot getSnapshot() {
        List<SnapshotInfo> citySnapshots = new ArrayList<>();
        // get snapshots of all cities
        for (String cityName : cityRepository.getAllCities().keySet()) {
            if (cityName.equals(ShardInfo.getShardInfo().getCityName())) {
                citySnapshots.add(grpcServer.getSnapshotImpl());
            } else {
                citySnapshots.add(grpcClient.getSnapshot(cityName));
            }
        }
        // combine the snapshots into a single system wide snapshot
        SystemSnapshot.Builder builder = SystemSnapshot.newBuilder();
        List<RideOfferInfo> rideOfferInfos = citySnapshots.stream().map(
                it -> it.getRideOffersList()
        ).flatMap(List::stream).collect(Collectors.toList());
        builder.addAllRideOffers(rideOfferInfos);

        // reconstruct ride requests
        Map<Timestamp, SystemSnapshot.PlannedPath.Builder> path_builders = new HashMap<>();
        List<SegmentInfo> segmentInfos = citySnapshots.stream().map(
                it -> it.getRideSegmentsList()
        ).flatMap(List::stream).collect(Collectors.toList());
        for (SegmentInfo segment : segmentInfos){
            if (!path_builders.containsKey(segment.getRequest().getRequestTimestamp())){
                Path initReqPath = Path.newBuilder().addAllSegments(new ArrayList<Path.Segment>()).build();
                Path initOfferPath = Path.newBuilder().addAllSegments(new ArrayList<Path.Segment>()).build();

                SystemSnapshot.PlannedPath.Builder tmp_builder = SystemSnapshot.PlannedPath.newBuilder()
                                                                .setGivenPath(initOfferPath)
                                                                .setRequestPath(initReqPath)
                                                                .setDate(segment.getRequest().getDate())
                                                                .setIsSatisfied(true);
                path_builders.put(segment.getRequest().getRequestTimestamp(),
                        tmp_builder);
            }
            SystemSnapshot.PlannedPath.Builder tmpBuilder = path_builders.get(segment.getRequest().getRequestTimestamp());
            tmpBuilder.setIsSatisfied(tmpBuilder.getIsSatisfied() && segment.getIsSatisfied());

            RideRequest currRequest = segment.getRequest();
            RideOffer currOffer = segment.getOffer();
            List<Path.Segment> newRequestPath = new ArrayList<>(tmpBuilder.getRequestPath().getSegmentsList());
            newRequestPath.add(
                    Path.Segment.newBuilder().setStartCityName(currRequest.getStartCityName())
                                                .setEndCityName(currRequest.getEndCityName())
                                                .setIndex(currRequest.getIndex()).build()
            );
            List<Path.Segment> newOffersPath = new ArrayList<>(tmpBuilder.getGivenPath().getSegmentsList());
            newOffersPath.add(
                    Path.Segment.newBuilder().setDriverName(currOffer.getPersonName()).setStartCityName(currOffer.getStartCityName()).setEndCityName(currOffer.getEndCityName()).build()
            );
            tmpBuilder.setRequestPath(Path.newBuilder().addAllSegments(newRequestPath));
            tmpBuilder.setGivenPath(Path.newBuilder().addAllSegments(newOffersPath));
        }

        List<SystemSnapshot.PlannedPath> paths = path_builders.values().stream().map(SystemSnapshot.PlannedPath.Builder::build).collect(Collectors.toList());
        builder.addAllPlannedPaths(paths);
        SystemSnapshot result = builder.build();
        return result;
    }

    @PostMapping("/rideOffers")
    public ResponseEntity<?> newRideOffer(@RequestBody RideOffer newRideOffer) throws RideOfferAlreadyExistsException {
        try {
            if (!newRideOffer.getStartCityName().equals(ShardInfo.getShardInfo().getCityName())) {
                log.info(ShardInfo.getShardInfo().getHostname() + " re-routing request from server in " + ShardInfo.getShardInfo().getCityName() + " to server from " + newRideOffer.getStartCityName());
                boolean result = grpcClient.offerRide(newRideOffer.getStartCityName(), newRideOffer);
                if (!result) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("couldn't add ride");
                }
            } else {
                if (ServerCommService.getAllRideOffers().contains(newRideOffer)) {
                    throw new RideOfferAlreadyExistsException();
                }
                // the leader will save and send everybody using zookeeper atomic bcast
                ShardInfo.getShardInfo().getZkService().broadcastRideOffer(newRideOffer);
            }
            return ResponseEntity.ok("ride added successfully");

        } catch (RideOfferAlreadyExistsException e) {
            throw e;
        } catch (StatusRuntimeException e) {
            if (e.getStatus() == Status.ALREADY_EXISTS) {
                throw new RideOfferAlreadyExistsException();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("couldn't add ride");
        }

    }

    @GetMapping("/pathPlanning")
    Path getPathPlanning(@RequestBody ArrayList<String> path, @RequestParam String date) {
        RideOffer ret;
        ArrayList<RideOffer> routePlan = new ArrayList<>();
        log.info("got request for path: " + path.toString() + " on date " + date);
        long millis = System.currentTimeMillis();
        Timestamp requestTimestamp = Timestamp.newBuilder().setSeconds(millis / 1000)
                .setNanos((int) ((millis % 1000) * 1000000)).build();
        try {

            for (int i = 0; i < path.size() - 1; i++) {
                // try to reserve space for each segment of the ride
                ret = dispatchPathSegment(path.get(i), path.get(i + 1), date, requestTimestamp,i);
                if (ret != null) {
                    routePlan.add(ret);
                } else {
                    log.info("Couldn't satisfy path planing ");
                    cancelRoute(routePlan, requestTimestamp);
                    throw new StatusRuntimeException(Status.NOT_FOUND);
                }
            }
            commitRequest(routePlan, requestTimestamp);
            List<Path.Segment> segments = routePlan.stream().map(it -> Path.Segment.newBuilder()
                    .setDriverName(it.getPersonName())
                    .setStartCityName(it.getStartCityName())
                    .setEndCityName(it.getEndCityName())
                    .build())
                    .collect(Collectors.toList());

            return Path.newBuilder().addAllSegments(segments).build();
        } catch (StatusRuntimeException e) {
            if (e.getStatus() != Status.NOT_FOUND)
                throw e;
            else {
                throw new PathPlanningNotFoundException();
            }
        } catch (Exception e) {

            log.info("got unexpected error:");
            e.printStackTrace();
            return null;
        }
    }

    void commitRequest(ArrayList<RideOffer> routePlan, Timestamp requestTimestamp) {
        for (RideOffer offer : routePlan) {
            RideRequest req = RideRequest.newBuilder()
                    .setRecursive(true)
                    .setStartCityName(offer.getStartCityName())
                    .setEndCityName(offer.getEndCityName())
                    .setDate(offer.getDepartureDate())
                    .setCancel(false)
                    .setCommit(true)
                    .setRequestTimestamp(requestTimestamp).setIndex(-1) // test
                    .build();
            if (ShardInfo.getShardInfo().getCityName().equals(offer.getStartCityName())) {
                grpcServer.commitOrAbortRideRequestImpl(req);
            } else {
                grpcClient.commitOrAbortRideRequest(req);
            }
        }
    }

    void cancelRoute(ArrayList<RideOffer> routePlan, Timestamp requestTimestamp) {
        for (RideOffer offer : routePlan) {
            RideRequest req = RideRequest.newBuilder()
                    .setRecursive(true)
                    .setStartCityName(offer.getStartCityName())
                    .setEndCityName(offer.getEndCityName())
                    .setDate(offer.getDepartureDate())
                    .setCancel(true)
                    .setCommit(false)
                    .setRequestTimestamp(requestTimestamp).setIndex(-1) // test
                    .build();
            if (ShardInfo.getShardInfo().getCityName().equals(offer.getStartCityName())) {
                grpcServer.commitOrAbortRideRequestImpl(req);
            } else {
                grpcClient.commitOrAbortRideRequest(req);
            }
        }
    }

    RideOffer dispatchPathSegment(String startCityName, String endCityName, String date, Timestamp requestTimestamp, int index) {
        RideRequest req = RideRequest.newBuilder()
                .setRecursive(true)
                .setStartCityName(startCityName)
                .setEndCityName(endCityName)
                .setDate(date)
                .setCancel(false)
                .setCommit(false)
                .setRequestTimestamp(requestTimestamp)
                .setIndex(index)
                .build();
        RideOffer possibleRide;
        if (ShardInfo.getShardInfo().getCityName().equals(startCityName)) {
            possibleRide = ServerCommService.askRideImpl(req);
            if (possibleRide == null) {
                possibleRide = ServerCommService.askRideFromAllOthers(req);
            }
        } else {
            possibleRide = grpcClient.askRide(startCityName, req);
        }
//        if (possibleRide != null)
//            log.info("possible ride:\n" + possibleRide.toString());
        return possibleRide;
    }
}