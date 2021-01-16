import host.controllers.RideOfferConfig;
import lombok.extern.slf4j.Slf4j;
import server_grpc.MainGrpcServer;
import host.MainRestServer;
import repositories.CityRepository;
import zkapi.ZooKeeperService;
import zkapi.utils.ShardInfo;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

@Slf4j
public class Main {
    // args[0] is the name of the city out of: Yaffo, Haifa, Jerusalem, TLV, Netanya
    // args[1] is the port number to listen on for gRPC server
    // args[2] is the address of the zookeeper server
    public static void main(String[] args) throws Exception {
//        if (args.length != 4){
//            System.out.print("Wrong number of arguments passed!");
//            System.exit(-1);
//        }

        try{

            CityRepository cityRep = new CityRepository();
            String cityName = args[0];
            int portGrpc = Integer.parseInt(args[1]);
            ShardInfo.getShardInfo().setHostname(getHostIP().concat(":").concat(Integer.toString(portGrpc)));
            RideOfferConfig.port = cityRep.getCityByName(cityName).getPort();
            String zkHost = args[2];
            initZkServiceAndShardInfo(zkHost, cityName);
            Runnable runGrpcServer =
                        () -> {
                            try {
                                MainGrpcServer.main(cityName, portGrpc, cityRep);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        };
            Thread threadGrpcServer = new Thread(runGrpcServer);
            threadGrpcServer.start();
            if (ShardInfo.getShardInfo().getZkService().isLeader()){
                // if the leader, start a new rust server
                Runnable runRestServer =
                        () -> { MainRestServer.main(cityName); };
                Thread threadRestServer = new Thread(runRestServer);
                threadRestServer.start();
            }

        } catch (Exception e){
            System.out.print(String.format("Error occured: %s", e.toString()));
            System.exit(-1);
        }


    }

    private static void initZkServiceAndShardInfo(String zkHost, String cityName) throws InterruptedException {
        String hostName = ShardInfo.getShardInfo().getHostname();

        //init zookeeper service
        ZooKeeperService zkService = new ZooKeeperService(zkHost, cityName);
        zkService.joinShardMembership(hostName, hostName);
        zkService.joinLeaderElection(hostName);
        zkService.startWatchingRideOffers();

        // init shard info
        ShardInfo.getShardInfo().setZkService(zkService);
        ShardInfo.getShardInfo().setCityName(cityName);
        ShardInfo.getShardInfo().setLiveNodes(zkService.getLiveNodesForCity(cityName));
        ShardInfo.getShardInfo().setShardLeader(zkService.getLeaderForCity(cityName));
        log.info(ShardInfo.getShardInfo().getHostname()+" Host " + hostName + " set leader as " + zkService.getLeaderForCity(cityName));
        TimeUnit.SECONDS.sleep(5); // sleep to let the system initialize
    }

    private static String getHostIP(){
        String ip;
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        }catch (UnknownHostException e) {
            throw new RuntimeException("couldn't find host ip");
        }
        return ip;
    }
}