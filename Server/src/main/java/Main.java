import server_grpc.MainGrpcServer;
import client_grpc.MainGrpcClient;
import host.MainRestServer;


public class Main {
    // args[1] is the name of the city out of: Yaffo, Haifa, Jerusalem, TLV
    public static void main(String[] args) throws Exception {
        Runnable runGrpcServer =
                    () -> {
                        try {
                            MainGrpcServer.main(args[0]);
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


    }
}