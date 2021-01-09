package entities;

import java.time.format.DateTimeFormatter;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrimaryKeyJoinColumn;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Primary;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Entity
@AllArgsConstructor
public class RideOffer {
    @Getter @Setter private String personName;
    @Getter @Setter private String phoneNumber;
    @Getter @Setter private String startCityName;
    @Getter @Setter private String endCityName;
    @Getter @Setter @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) private Date departureDate;
    @Getter @Setter private int vacancies;
    @Getter @Setter private int permittedDeviation;

    public RideOffer() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RideOffer rideOffer = (RideOffer) o;
        return personName == rideOffer.personName &&
                phoneNumber == rideOffer.phoneNumber &&
                startCityName == rideOffer.startCityName &&
                endCityName == rideOffer.endCityName &&
                vacancies == rideOffer.vacancies &&
                permittedDeviation == rideOffer.permittedDeviation &&
                departureDate == rideOffer.departureDate;
    }

    @Override
    public int hashCode() {
        return Objects.hash(personName, phoneNumber, startCityName, endCityName);
    }
}