package client_grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.Map;
import java.util.HashMap;
import java.util.Scanner;
//import entities.RideOffer;
import generated.RideOffer;
import generated.RideRequest;

public class MainGrpcClient {

    private static Map<String, String> mapCitiesPorts=new HashMap<>();  
    
    public static void initMap(){
        if (!mapCitiesPorts.isEmpty())
            return;
        mapCitiesPorts.put("TLV", "8001");
        mapCitiesPorts.put("Jerusalem", "8002");
        mapCitiesPorts.put("Haifa", "8003");
        mapCitiesPorts.put("Yaffo", "8004");
    }
    
    public static void offerRide(String cityName, RideOffer newRideOffer) {
        initMap();
        String portNum = mapCitiesPorts.get(cityName);
        String target = "localhost:" + portNum;
        ManagedChannel channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
        ServerCommClient client = new ServerCommClient(channel);
        client.offerRide(newRideOffer);
        System.out.println("Waiting");
        Scanner sc= new Scanner(System.in);
        sc.next();
    }

    public static void askRide(String cityName, RideRequest newRideRequest) {
        initMap();
        String portNum = mapCitiesPorts.get(cityName);
        String target = "localhost:" + portNum;
        ManagedChannel channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
        ServerCommClient client = new ServerCommClient(channel);
        client.askRide(newRideRequest);
        System.out.println("Waiting");
        Scanner sc= new Scanner(System.in);
        sc.next();
    }
}
