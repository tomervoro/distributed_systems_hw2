package zkapi.utils;

import java.util.ArrayList;
import java.util.List;

import zkapi.ZooKeeperService;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShardInfo {
    public static final String ELECTION = "/election";
    public static final String MEMBERSHIP = "/membership";
    private static final ShardInfo shardInfo = new ShardInfo();

    private ZooKeeperService zkService;
    private List<String> liveNodes = new ArrayList<>();
    private String shardLeader;

    public static ShardInfo getShardInfo(){
        return shardInfo;
    }


}
