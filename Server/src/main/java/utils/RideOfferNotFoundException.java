package utils;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
// This annotation tell Spring to convert this exception to HttpStatus.NOT_FOUND
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class RideOfferNotFoundException extends  RuntimeException {

    public RideOfferNotFoundException(Long id){
        super("Could not find employee " + id);
    }

}
