package host;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter;
import repositories.RideOfferRepository;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
public class MainRestServer {
    public static void main(String[] args) {
        RideOfferRepository.setDefaultName(args[0]);
        SpringApplication.run(MainRestServer.class, args);
    }
    @Bean
    ProtobufHttpMessageConverter protobufHttpMessageConverter() {
        return new ProtobufHttpMessageConverter();
    }
}
