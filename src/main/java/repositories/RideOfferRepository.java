package repositories;

//import entities.RideOffer;
import generated.RideOffer;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Repository;
import utils.RideOfferAlreadyExistsException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Repository
public class RideOfferRepository {

    private ArrayList<RideOffer> rideOffers;
    private long idIndex = 1;
    @Setter @Getter private String cityName;
    @Setter @Getter private static String defaultName;

    public RideOfferRepository(){
        this(defaultName);
    }
    
    public RideOfferRepository(String init_cityName) {
        rideOffers = new ArrayList<>();
        cityName = init_cityName;
    }

    public List<RideOffer> findAll() {
        return rideOffers;
    }

    public void save(RideOffer newRideOffer) throws RideOfferAlreadyExistsException {
        if (rideOffers.contains(newRideOffer)) {
            throw new RideOfferAlreadyExistsException();
        }
        saveRideOffer(newRideOffer);
    }

    public void delete(RideOffer rideOffer) {
        rideOffers.remove(rideOffer);
    }

    private void saveRideOffer(RideOffer rideOffer){
        rideOffers.add(rideOffer);
    }
}