package zkapi;

import com.google.protobuf.InvalidProtocolBufferException;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;
import org.apache.tomcat.util.codec.binary.StringUtils;
import server_grpc.ServerCommService;
import zkapi.listeners.LeaderChangeListener;
import zkapi.listeners.MembershipChangeListener;
import zkapi.utils.ShardInfo;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;

import generated.RideOffer;

import java.util.List;

@Slf4j
public class ZooKeeperService {
    private ZkClient zkClient;
    private String cityName;
    public ZooKeeperService(String host, String cityName){
        // initialize the zookeeper client
        zkClient = new ZkClient(host, 3000, 3000, new ZkSerializer() {
            @Override
            public byte[] serialize(Object o) throws ZkMarshallingError {
                if (o instanceof String)
                    return ((String) o).getBytes();
                else if (o instanceof RideOffer){
                    return ((RideOffer) o).toByteArray();
                }else{
                    return null;
                }
            }

            @Override
            public Object deserialize(byte[] bytes) throws ZkMarshallingError {
                if (bytes == null) {
                    return null;
                } else {
                    try{
                        return RideOffer.parseFrom(bytes);
                    } catch (InvalidProtocolBufferException e) {
                        // not a protobuf
                    }
                    return StringUtils.newStringUtf8(bytes);
                }
            }
        });
        this.cityName = cityName;
        // make sure all the parent folders exist
        if (!zkClient.exists(ShardInfo.MEMBERSHIP)) {
            zkClient.createPersistent(ShardInfo.MEMBERSHIP , "all live nodes are displayed here");
        }
        if (!zkClient.exists(ShardInfo.ELECTION)) {
            zkClient.createPersistent(ShardInfo.ELECTION , "node for group membership");
        }
        if (!zkClient.exists(ShardInfo.RIDE_OFFERS)) {
            zkClient.createPersistent(ShardInfo.RIDE_OFFERS , "node for sharing ride offers");
        }
    }

    public Object getZnodeData(String path) {
        return zkClient.readData(path);
    }

    public void joinLeaderElection(String name){
        if (!zkClient.exists(ShardInfo.ELECTION.concat("/").concat(cityName)) ){
            // election not started yet
            zkClient.createPersistent(ShardInfo.ELECTION.concat("/").concat(cityName), "node for leader election");
        }
        zkClient.createEphemeralSequential(ShardInfo.ELECTION.concat("/").concat(cityName).concat("/node"), name);
        zkClient.subscribeChildChanges(ShardInfo.ELECTION.concat("/").concat(cityName), new LeaderChangeListener());
    }

    public void joinShardMembership(String name, String data){
        if (!zkClient.exists(ShardInfo.MEMBERSHIP.concat("/").concat(cityName)) ){
            // membership not initialized yet
            zkClient.createPersistent(ShardInfo.MEMBERSHIP.concat("/").concat(cityName), "node for group membership");
        }
        String path = ShardInfo.MEMBERSHIP.concat("/").concat(cityName).concat("/").concat(name);
        if (zkClient.exists(path)){
            // name already in shard, do nothing
            return;
        }
        zkClient.createEphemeral(path, data);
        zkClient.subscribeChildChanges(ShardInfo.MEMBERSHIP.concat("/").concat(cityName), new MembershipChangeListener());
    }

    public List<String> getLiveNodesForCity(String cityName){
        if (!zkClient.exists(ShardInfo.MEMBERSHIP.concat("/").concat(cityName)) ){
            throw new RuntimeException("city " + cityName + " does not exists");
        }
        List<String> liveNodes = zkClient.getChildren(ShardInfo.MEMBERSHIP.concat("/").concat(cityName));
        if (liveNodes.isEmpty()){
            throw new RuntimeException("No live nodes for this city");
        }
        
        return liveNodes;
    }

    public String getLeaderForCity(String cityName){
//        log.info("city name: " + cityName);
        if (!zkClient.exists(ShardInfo.ELECTION.concat("/").concat(cityName))){
            throw new RuntimeException("election for city " + cityName + "does now exists");
        }
        // because we use sequential nodes we cant save the name as part of the node name, instead we save it as the node data
        List<String> aliveNodes = zkClient.getChildren(ShardInfo.ELECTION.concat("/").concat(cityName));
        String leader = aliveNodes.get(0);
//        log.info("Leader node is: " + leader);
        return (String) ShardInfo.getShardInfo().getZkService().getZnodeData(ShardInfo.ELECTION.concat("/").concat(cityName).concat("/").concat(leader));
    }


    public void broadcastRideOffer(RideOffer rideOffer){
        if (!zkClient.exists(ShardInfo.RIDE_OFFERS.concat("/").concat(cityName))){
            // ride offers not initialized yet
            zkClient.createPersistent(ShardInfo.RIDE_OFFERS.concat("/").concat(cityName), "node for group membership");;
        }
        // TODO: maybe change back rides to be persistent
//        zkClient.createPersistentSequential(ShardInfo.RIDE_OFFERS.concat("/").concat(cityName).concat("/").concat("offer"),rideOffer);
        zkClient.createEphemeralSequential(ShardInfo.RIDE_OFFERS.concat("/").concat(cityName).concat("/").concat("offer"),rideOffer);
        log.info(ShardInfo.getShardInfo() + " bcasted offer from " + rideOffer.getPersonName() + " to followers");
    }


    public void startWatchingRideOffers(){
        if (!zkClient.exists(ShardInfo.RIDE_OFFERS.concat("/").concat(cityName))){
            // ride offers not initialized yet
            zkClient.createPersistent(ShardInfo.RIDE_OFFERS.concat("/").concat(cityName), "node for group membership");;
        }
        zkClient.subscribeChildChanges(ShardInfo.RIDE_OFFERS.concat("/").concat(cityName), new ServerCommService.RideOfferListener());
    }

    public boolean isLeader(){
        return getLeaderForCity(cityName).equals(ShardInfo.getShardInfo().getHostname());
    }

}