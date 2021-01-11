package repositories;

import entities.City;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

public class CityRepository {
    private Map<String, City> cities=new HashMap<>();


    public CityRepository(){
        initCities();
    }
    public void initCities(){

        if (!cities.isEmpty()){
            return;
        }
        List<Integer> TLV_ports = new ArrayList<Integer>(){{
            add(8001);
            add(7001);
            add(6001);
        }};
        City telAviv = new City(0, 0, "TLV", TLV_ports);
        
        List<Integer> netanya_ports = new ArrayList<Integer>(){{
            add(8002);
            add(7002);
            add(6002);
        }};
        City netanya = new City(1, 0, "Netanya", netanya_ports);

        List<Integer> haifa_ports = new ArrayList<Integer>(){{
            add(8003);
            add(7003);
            add(6003);
        }};
        City haifa = new City(4, 3, "Haifa", haifa_ports);

        List<Integer> jerusalem_ports = new ArrayList<Integer>(){{
            add(8004);
            add(7004);
            add(6004);
        }};
        City jerusalem = new City(0, 2, "Jerusalem", jerusalem_ports);

        List<Integer> yaffo_ports = new ArrayList<Integer>(){{
            add(8005);
            add(7005);
            add(6005);
        }};
        City yaffo = new City(1, 1, "Yaffo", yaffo_ports);

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
