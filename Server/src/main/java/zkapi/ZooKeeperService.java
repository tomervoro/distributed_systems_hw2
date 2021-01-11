package zkapi;

import org.apache.zookeeper.CreateMode;
import zkapi.utils.ShardInfo;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;


public class ZooKeeperService {
    private ZkClient zkClient;
    private String cityName;


    public ZooKeeperService(String host, String cityName){
        zkClient = new ZkClient(host, 1000, 1000);
    }

    public String getZnodeData(String path){
        return zkClient.readData(path);
    }

    public void registerChildWatcher(String path, IZkChildListener listener ){
        zkClient.subscribeChildChanges(path, listener);
    }

    public void joinLeaderElection(String data){
        if (!zkClient.exists(ShardInfo.ELECTION.concat("/").concat(cityName)) ){
            // election not started yet
            zkClient.create(ShardInfo.ELECTION.concat("/").concat(cityName), "node for leader election", CreateMode.PERSISTENT);
        }
        zkClient.create(ShardInfo.ELECTION.concat("/").concat(cityName).concat("/node"), data, CreateMode.EPHEMERAL_SEQUENTIAL);
    }

    public void joinShardMembership(String name, String data){
        if (!zkClient.exists(ShardInfo.MEMBERSHIP.concat("/").concat(cityName)) ){
            // membership not initialized yet
            zkClient.create(ShardInfo.MEMBERSHIP.concat("/").concat(cityName), "node for group membership", CreateMode.PERSISTENT);
        }
        String path = ShardInfo.MEMBERSHIP.concat("/").concat(cityName).concat("/").concat(name);
        if (zkClient.exists(path)){
            // name already in shard, do nothing
            return;
        }
        zkClient.create(path, data, CreateMode.EPHEMERAL);
    }




}