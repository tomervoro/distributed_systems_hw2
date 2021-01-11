import server_grpc.MainGrpcServer;
import client_grpc.MainGrpcClient;
import host.MainRestServer;
import repositories.CityRepository;


public class Main {
    // args[0] is the name of the city out of: Yaffo, Haifa, Jerusalem, TLV, Netanya
    // args[1] is the port number to listen on (check out "CityRepository" for more info)
    public static void main(String[] args) throws Exception {
        if (args.length != 2){
            System.out.print("Wrong number of arguments passed!");
            System.exit(-1);
        }

        try{
            CityRepository cityRep = new CityRepository();
            String cityName = args[0];
            int port = Integer.parseInt(args[1]);
            Runnable runGrpcServer =
                        () -> {
                            try {
                                MainGrpcServer.main(cityName, port, cityRep);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        };
            Thread threadGrpcServer = new Thread(runGrpcServer);
            threadGrpcServer.start();
            
            Runnable runRestServer =
                        () -> { MainRestServer.main(args); };
            Thread threadRestServer = new Thread(runRestServer);
            threadRestServer.start();
        } catch (Exception e){
            System.out.print(String.format("Error occured: %s", e.toString()));
            System.exit(-1);
        }


    }
}