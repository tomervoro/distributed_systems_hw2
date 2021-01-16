package zkapi.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import zkapi.ZooKeeperService;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class ShardInfo {
    public static final String ELECTION = "/election";
    public static final String MEMBERSHIP = "/membership";
    public static final String RIDE_OFFERS = "/ride_offers";
    private static final ShardInfo shardInfo = new ShardInfo();

    private ZooKeeperService zkService;
    private String cityName;
    private List<String> liveNodes = Collections.synchronizedList(new ArrayList<String>());
    private String shardLeader;
    private String hostname;

    public static ShardInfo getShardInfo(){
        return shardInfo;
    }

    public  String getCityName(){
        return cityName;
    }


}
