package server_grpc;

import java.util.Map;
import java.util.HashMap;

import entities.City;
import repositories.CityRepository;

import static java.lang.Math.pow;

public class MainGrpcServer{

    public static void main(String cityName) throws Exception {
        CityRepository cityRep = new CityRepository();
        int portToListen = cityRep.getCityByName(cityName).getPort();
        ServerCommServer server = new ServerCommServer(portToListen, cityRep, cityName);
        server.start();
        System.out.println("Server started");
        server.blockUntilShutdown();
    }
}
