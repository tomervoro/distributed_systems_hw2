package repositories;

import entities.City;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class CityRepository {
    private Map<String, City> cities=new HashMap<>();


    public CityRepository(){
        initCities();
    }
    public void initCities(){

        if (!cities.isEmpty()){
            return;
        }

        City telAviv = new City(0, 0, "TLV", 8001);
        City netanya = new City(1, 0, "Netanya", 8002);
        City haifa = new City(4, 3, "Haifa", 8003);
        City jerusalem = new City(0, 2, "Jerusalem", 8004);
        City yaffo = new City(1, 1, "Yaffo", 8005);

        cities.put("TLV", telAviv);
        cities.put("Netanya", netanya);
        cities.put("Haifa", haifa);
        cities.put("Jerusalem", jerusalem);
        cities.put("Yaffo", yaffo);
    }


    public City getCityByName(String cityName){
        return cities.get(cityName);
    }

    public Map<String, City> getAllCities(){
        return cities;
    }
}
