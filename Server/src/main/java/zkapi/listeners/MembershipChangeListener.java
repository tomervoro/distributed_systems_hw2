package zkapi.listeners;

import java.util.List;

import zkapi.utils.ShardInfo;
//import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.IZkChildListener;

/**
 * this is a listener meant to watch over membership changes in a shard
 */
public class MembershipChangeListener  implements IZkChildListener {

    @Override
    public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
        if (currentChilds.isEmpty()){
            throw new RuntimeException("shard with membership of 0 nodes");
        }
        // reset all the live nodes and change the lives nodes to the new state of the system
        ShardInfo.getShardInfo().getLiveNodes().clear();
        ShardInfo.getShardInfo().getLiveNodes().addAll(currentChilds);

    }
}
