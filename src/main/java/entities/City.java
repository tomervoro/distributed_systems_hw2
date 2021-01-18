package entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.List;

@AllArgsConstructor
public class City {
    @Getter @Setter private long x;
    @Getter @Setter private long y;
    @Getter @Setter private String name;
    @Getter @Setter private long id;
    @Getter @Setter private int port;

    private static long cityCount = 0;
    public City(long x, long y, String name, int port) {
        this.x = x;
        this.y = y;
        this.name = name;
        this.id = cityCount++;
        this.port = port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        City city = (City) o;
        return name.equals(city.name) &&
                id == city.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, id);
    }
}
