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

        City telAviv = new City(0, 0, "TLV", 21000);

        City netanya = new City(1, 0, "Netanya", 21001);

        City haifa = new City(1, 1, "Haifa", 21002);
//
//        City jerusalem = new City(0, 2, "Jerusalem", 21003);
//
//        City yaffo = new City(0, 1, "Yaffo", 21004);

        cities.put("TLV", telAviv);
        cities.put("Netanya", netanya);
        cities.put("Haifa", haifa);
//        cities.put("Jerusalem", jerusalem);
//        cities.put("Yaffo", yaffo);
    }


    public City getCityByName(String cityName){
        return cities.get(cityName);
    }

    public Map<String, City> getAllCities(){
        return cities;
    }
}

//package repositories;
//
//import java.util.ArrayList;
//import java.util.Map;
//import java.util.HashMap;
//import java.util.List;
//
//public class CityRepository {
//    private Map<String, Integer> citiesPosts=new HashMap<>();
//
//
//    public CityRepository(){
//        initCities();
//    }
//
//    public void initCities(){
//
//        if (!citiesPosts.isEmpty()){
//            return;
//        }
//
//        citiesPosts.put("TLV", 6000);
//        citiesPosts.put("Netanya", 7000);
//        citiesPosts.put("Haifa", 8000);
//        citiesPosts.put("Jerusalem", 9000);
//        citiesPosts.put("Yaffo", 10000);
//    }
//
//
//    public int getPortByName(String cityName){
//        return citiesPosts.get(cityName);
//    }
//
//    public Map<String, Integer> getAllCitiesPorts(){
//        return citiesPosts;
//    }
//}

