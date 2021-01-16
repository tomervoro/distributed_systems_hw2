package utils;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
// This annotation tell Spring to convert this exception to HttpStatus.NOT_FOUND
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class PathPlanningNotFoundException extends  RuntimeException {

    public PathPlanningNotFoundException(){
        super("Could not find rides to satisfy path planning" );
    }

}
