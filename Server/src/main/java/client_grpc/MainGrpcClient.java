package client_grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.Map;
import java.util.HashMap;
import java.util.Scanner;
//import entities.RideOffer;
import generated.RideOffer;
import generated.RideRequest;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class MainGrpcClient {

    private static Map<String, List<String>> mapCitiesPorts=new HashMap<>();
    
    public static void initMap(){
        if (!mapCitiesPorts.isEmpty())
            return;


        List<String> TLV_ports = new ArrayList<String>(){{
            add("8001");
            add("7001");
            add("6001");
        }};
        mapCitiesPorts.put("TLV", TLV_ports);


        List<String> netanya_ports = new ArrayList<String>(){{
            add("8002");
            add("7002");
            add("6002");
        }};
        mapCitiesPorts.put("Netanya", netanya_ports);


        List<String> haifa_ports = new ArrayList<String>(){{
            add("8003");
            add("7003");
            add("6003");
        }};
        mapCitiesPorts.put("Haifa", haifa_ports);


        List<String> jerusalem_ports = new ArrayList<String>(){{
            add("8004");
            add("7004");
            add("6004");
        }};
        mapCitiesPorts.put("Jerusalem", jerusalem_ports);


        List<String> yaffo_ports = new ArrayList<String>(){{
            add("8005");
            add("7005");
            add("6005");
        }};
        mapCitiesPorts.put("Yaffo", yaffo_ports);
    }
    
    public static void offerRide(String cityName, RideOffer newRideOffer) {
        initMap();
        Random rand = new Random();
        String randomPortNum = mapCitiesPorts.get(cityName).get(rand.nextInt(mapCitiesPorts.get(cityName).size()));
        String target = "localhost:" + randomPortNum;
        ManagedChannel channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
        ServerCommClient client = new ServerCommClient(channel);
        client.offerRide(newRideOffer);
        System.out.println("Waiting");
        Scanner sc= new Scanner(System.in);
        sc.next();
    }

    public static void askRide(String cityName, RideRequest newRideRequest) {
        initMap();
        Random rand = new Random();
        String randomPortNum = mapCitiesPorts.get(cityName).get(rand.nextInt(mapCitiesPorts.get(cityName).size()));
        String target = "localhost:" + randomPortNum;
        ManagedChannel channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
        ServerCommClient client = new ServerCommClient(channel);
        client.askRide(newRideRequest);
        System.out.println("Waiting");
        Scanner sc= new Scanner(System.in);
        sc.next();
    }
}
