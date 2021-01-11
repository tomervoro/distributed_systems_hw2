package host.controllers;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import client_grpc.MainGrpcClient;
import entities.City;
//import entities.RideOffer;
import generated.RideOffer;
import lombok.Data;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.*;
import repositories.RideOfferRepository;
import utils.RideOfferAlreadyExistsException;
import utils.RideOfferNotFoundException;

@RestController
public class RideOfferController {

    private final RideOfferRepository repository;

    public RideOfferController() {
        this.repository = new RideOfferRepository();
    }

    @GetMapping("/rideOffers")
    List<RideOffer> all() {
        return repository.findAll();
    }

    @PostMapping("/rideOffers")
    void newRideOffer(@RequestBody RideOffer newRideOffer) throws RideOfferAlreadyExistsException {
            if (newRideOffer.getStartCityName() != repository.getCityName()){
                Runnable runGrpcClient =
                        () -> { MainGrpcClient.offerRide(repository.getCityName(), newRideOffer); };
                Thread threadGrpcClient = new Thread(runGrpcClient);
                threadGrpcClient.start();
            }
            else{
                repository.save(newRideOffer);
            }
    }
    @PostMapping("/pathPlanning")
    List<RideOffer> getPathPlanning(@RequestParam ArrayList<String> path, @RequestParam Date date){
        boolean ret;
        ArrayList<RideOffer> resposne = new ArrayList<>();
        for (int i= 0; i< path.size()-1; i++){
            // try to reserve space for each segment of the ride
            ret = dispatchPathSegment(path.get(i), path.get(i+1), date);
            if (!ret){
                // TODO - to implement
            }
        }
        return resposne;
    }

    boolean dispatchPathSegment(String startCityName, String endCityName, Date date){
        if (startCityName != repository.getCityName()) {
//            Runnable runGrpcClient =
//                    () -> { MainGrpcClient.main(, date); };
//            Thread threadGrpcClient = new Thread(runGrpcClient);
//            threadGrpcClient.start();
            return false;
        }else{
            return saveSpot(endCityName, date);
        }

    }

    boolean saveSpot(String destCity, Date date){
        // TODO: to implement
        return false;
    }

}