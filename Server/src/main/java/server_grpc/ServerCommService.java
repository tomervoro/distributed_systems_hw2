package server_grpc;

import generated.*;
import io.grpc.stub.StreamObserver;
import javafx.util.Pair;
import repositories.CityRepository;
import entities.City;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import java.sql.Timestamp;
import java.util.Date;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static java.lang.Math.abs;
import client_grpc.MainGrpcClient;

public class ServerCommService extends ServerCommGrpc.ServerCommImplBase {
    private final ConcurrentMap<Date, ConcurrentMap<RideOffer, Integer>> ridesVacancies = new ConcurrentHashMap<>();
    private final ConcurrentMap<Date, List<RideOffer>> rides = new ConcurrentHashMap<Date, List<RideOffer>>();
    private final ConcurrentMap<RideRequest, Pair<RideOffer, Timestamp>> rideRequests = new ConcurrentHashMap<>();
    private final CityRepository cityRepository;
    private final String currCityName;
    public static final int secondsToTimeout = 30;

    public ServerCommService(CityRepository cityRepository, String currCityName){
        this.cityRepository = cityRepository;
        this.currCityName = currCityName;
    }

    private double calcDist(City P1, City P2, City P0){
        // calc distance between point P0 and line created py two point P1 and P2
        return abs((P2.getX() - P1.getX())*(P1.getY() - P0.getY()) - (P1.getX() - P0.getX())*(P2.getY() - P1.getY())) / sqrt(pow((P2.getX()-P1.getX()), 2) + pow(P2.getY()-P1.getY(), 2));
    }

    // Server streaming
    @Override
    public void offerRide(RideOffer newRideOffer, StreamObserver<ServerResponse> responseObserver) {
        // Calculate the rectangle boundaries
        String personName = newRideOffer.getPersonName();
        String phoneNumber = newRideOffer.getPhoneNumber();
        String startCityName = newRideOffer.getStartCityName();
        int vacancies = newRideOffer.getVacancies();
        int permittedDeviation = newRideOffer.getPermittedDeviation();
        com.google.protobuf.Timestamp departureDateTimestamp = newRideOffer.getDepartureDate();
        Date departureDate = convertTimestampToDate(departureDateTimestamp);

        // add vacancies
        ConcurrentMap<RideOffer, Integer> currVacanciesOnDate = ridesVacancies.getOrDefault(departureDate, new ConcurrentHashMap<>());
        currVacanciesOnDate.put(newRideOffer, vacancies);
        ridesVacancies.put(departureDate, currVacanciesOnDate);

        // add ride details
        rides.get(departureDate).add(newRideOffer);

        ServerResponse response = ServerResponse.newBuilder().setResult(ServerResponse.Result.SUCCESS).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
    
    
    private void updateVacancies(Date date, RideOffer newRideOffer, int diff){
        ConcurrentMap<RideOffer, Integer> currVacanciesOnDate = ridesVacancies.get(date);
        currVacanciesOnDate.put(newRideOffer, currVacanciesOnDate.get(newRideOffer) + diff);
        ridesVacancies.put(date, currVacanciesOnDate);
    }

    @Override
    public void askRide(RideRequest req, StreamObserver<RideOffer> responseObserver) {
        // TODO: handle response
        Date date = convertTimestampToDate(req.getDate());
        if (!rides.containsKey(date)){
            return;
        }

        Boolean flagFound = false;
        ConcurrentMap<RideOffer, Integer> freePlaces = ridesVacancies.get(date);
        Map<String, City> allCitiesMap = cityRepository.getAllCities();
        for (Map.Entry<RideOffer, Integer> entry : freePlaces.entrySet()){
            RideOffer possibleRide = entry.getKey();
            int offererVacancies = entry.getValue();

            if (possibleRide.getEndCityName().equals(req.getEndCityName()) && offererVacancies > 0){
                if (!possibleRide.getStartCityName().equals(req.getStartCityName())){
                    double dist = calcDist(allCitiesMap.get(possibleRide.getStartCityName()),
                                           allCitiesMap.get(possibleRide.getEndCityName()),
                                           allCitiesMap.get(req.getStartCityName()));
                    if (dist > possibleRide.getPermittedDeviation()){
                        continue;
                    }
                }

                updateVacancies(date, possibleRide, -1);
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                rideRequests.put(req, new Pair<RideOffer, Timestamp>(possibleRide, timestamp));
                flagFound = true;
                responseObserver.onNext(possibleRide);
                break;
            }
        }

        if (!flagFound && req.getRecursive()){
            for (String cityName : cityRepository.getAllCities().keySet()){
                RideRequest new_req = RideRequest.newBuilder()
                                    .setRecursive(false)
                                    .setStartCityName(req.getStartCityName())
                                    .setEndCityName(req.getEndCityName())
                                    .setDate(req.getDate())
                                    .build();
                MainGrpcClient.askRide(cityName, req);
            }
        }


        responseObserver.onCompleted();
    }

    @Override
    public void commitOrAbortRideRequest(RideRequest req, StreamObserver<ServerResponse> responseObserver) {
        Date date = convertTimestampToDate(req.getDate());
        if (!rides.containsKey(date)){
            return;
        }

        Pair<RideOffer, Timestamp> match = rideRequests.get(req);
        rideRequests.remove(req);
        boolean commit = req.getCommit();

        if (commit){
            Timestamp req_timestamp = match.getValue();
            Timestamp curr_timestamp = new Timestamp(System.currentTimeMillis());

            long milliseconds = curr_timestamp.getTime() - req_timestamp.getTime();
		    int seconds_passed = (int) milliseconds / 1000;
            if (seconds_passed > secondsToTimeout){ commit = true;}
        }
        else{
            // else- abort
            RideOffer rideOffer = match.getKey();
            ridesVacancies.get(date).remove(req);
            rides.remove(req);
            updateVacancies(date, rideOffer, 1);
        }

        responseObserver.onCompleted();
    }

    private Date convertTimestampToDate(com.google.protobuf.Timestamp timestamp){

        return new Date(timestamp.getSeconds() * 1000);
    }

}
