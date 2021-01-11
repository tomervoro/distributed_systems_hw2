package zkapi.listeners;

import java.util.List;

import zkapi.utils.ShardInfo;
import lombok.Setter;
import org.I0Itec.zkclient.IZkChildListener;

/**
 * this is a listener meant to watch over leader changes in a shard
 */
@Setter
public class LeaderChangeListener  implements IZkChildListener {

    @Override
    public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
        if (currentChilds.isEmpty()){
            throw new RuntimeException("No new nodes left in cluster to be leader");
        }
        String newLeader = currentChilds.get(0);
        String leaderName = ShardInfo.getShardInfo().getZkService().getZnodeData(parentPath+ "/" + newLeader);

        ShardInfo.getShardInfo().setShardLeader(leaderName);

    }
}
