package host;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.protobuf.ProtobufJsonFormatHttpMessageConverter;
import repositories.RideOfferRepository;
import java.util.Collections;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
public class MainRestServer {
    public static void main(String cityName) {
        RideOfferRepository.setDefaultName(cityName);
        SpringApplication app = new SpringApplication(MainRestServer.class);
        app.run();
    }

   

}
