package client_grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.Map;
import java.util.HashMap;
import java.util.Scanner;
import generated.RideOffer;
import generated.RideRequest;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import lombok.extern.slf4j.Slf4j;
import utils.RideOfferAlreadyExistsException;
import zkapi.ZooKeeperService;
import zkapi.utils.ShardInfo;
import generated.SnapshotInfo;

@Slf4j
public class MainGrpcClient {
    private ZooKeeperService zkApi;
    private ConcurrentMap<String, ManagedChannel> openChannels;
    public MainGrpcClient(String cityName){
//        this.zkApi = new ZooKeeperService("localhost:2181", cityName);
        zkApi = ShardInfo.getShardInfo().getZkService();
        openChannels = new ConcurrentHashMap<String, ManagedChannel>();
    }
    
    public boolean offerRide(String cityName, RideOffer newRideOffer) throws RideOfferAlreadyExistsException {
        return offerRide(cityName, newRideOffer, "");
    }

    public boolean offerRide(String cityName, RideOffer newRideOffer, String target) throws RideOfferAlreadyExistsException {
        if (target.equals("")) {
            target = getRandomTarget(cityName);
        }
        log.info(ShardInfo.getShardInfo().getHostname() + ": sending ride offer to " + target);
        ManagedChannel channel = getChannelForTarget(target);
        ServerCommClient distServer = new ServerCommClient(channel);
        return distServer.offerRide(newRideOffer);
    }

    public RideOffer askRide(String cityName, RideRequest newRideRequest) {
        return askRide(cityName, newRideRequest, "");
    }

    public RideOffer askRide(String cityName, RideRequest newRideRequest, String target) {
        if (target.equals("")) {
            target = getRandomTarget(cityName);
        }
        ManagedChannel channel = getChannelForTarget(target);
        ServerCommClient distServer = new ServerCommClient(channel);
        RideOffer rideOffer = distServer.askRide(newRideRequest);
        return rideOffer;
    }

    public SnapshotInfo getSnapshot(String cityName) {
        String target = getRandomTarget(cityName);
        ManagedChannel channel = getChannelForTarget(target);
        ServerCommClient distServer = new ServerCommClient(channel);
        SnapshotInfo snapshotInfo = distServer.getSnapshot();
        return snapshotInfo;
    }

    private String getRandomTarget(String cityName){
        Random rand = new Random();
        List<String> liveNodes = zkApi.getLiveNodesForCity(cityName);
        String randomTarget = liveNodes.get(rand.nextInt(liveNodes.size()));
        return randomTarget;
    }

    public void commitOrAbortRideRequest(RideRequest req){
        String target = ShardInfo.getShardInfo().getZkService().getLeaderForCity(req.getStartCityName());
        ManagedChannel channel = getChannelForTarget(target);
        ServerCommClient distServer = new ServerCommClient(channel);
        distServer.commitOrAbortRideRequest(req);
    }

    public ManagedChannel getChannelForTarget(String target){
        ManagedChannel channel;
        if (openChannels.containsKey(target)){
            channel = openChannels.get(target);
        }else{
            channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
            openChannels.put(target, channel);
        }
        return channel;
    }
}
