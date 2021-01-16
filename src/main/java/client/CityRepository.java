//
//import java.util.ArrayList;
//import java.util.Map;
//import java.util.HashMap;
//import java.util.List;
//
//public class CityRepository {
//    private Map<String, String> citiesPosts=new HashMap<>();
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
//        citiesPosts.put("TLV", "6000");
//        citiesPosts.put("Netanya", "7000");
//        citiesPosts.put("Haifa", "8000");
//        citiesPosts.put("Jerusalem", "9000");
//        citiesPosts.put("Yaffo", "10000");
//    }
//
//
//    public String getPortByName(String cityName){
//        return citiesPosts.get(cityName);
//    }
//
//    public Map<String, String> getAllCitiesPorts(){
//        return citiesPosts;
//    }
//}
