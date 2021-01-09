package utils;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
// This annotation tell Spring to convert this exception to HttpStatus.NOT_ACCEPTABLE
@ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE)
public class RideOfferAlreadyExistsException extends Exception{
    public RideOfferAlreadyExistsException(String name){
        super("RideOffer by " + name + "already exist" );
    }

}
