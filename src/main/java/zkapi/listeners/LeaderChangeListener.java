package zkapi.listeners;

import java.util.Collections;
import java.util.List;

import host.MainRestServer;
import lombok.extern.slf4j.Slf4j;
import zkapi.utils.ShardInfo;
import lombok.Setter;
import org.I0Itec.zkclient.IZkChildListener;

/**
 * this is a listener meant to watch over leader changes in a shard
 */
@Slf4j
@Setter
public class LeaderChangeListener  implements IZkChildListener {

    @Override
    public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
        if (currentChilds.isEmpty()){
            throw new RuntimeException("No new nodes left in cluster to be leader");
        }
        Collections.sort(currentChilds);
        String newLeader = currentChilds.get(0);
        String leaderName = (String) ShardInfo.getShardInfo().getZkService().getZnodeData(parentPath+ "/" + newLeader);
        if (!leaderName.equals(ShardInfo.getShardInfo().getShardLeader()) &&
                ShardInfo.getShardInfo().getZkService().isLeader()){
            // if the leader, start a new rust server
            Runnable runRestServer =
                    () -> { MainRestServer.main(ShardInfo.getShardInfo().getCityName()); };
            Thread threadRestServer = new Thread(runRestServer);
            threadRestServer.start();
        }
        ShardInfo.getShardInfo().setShardLeader(leaderName);
        log.info(ShardInfo.getShardInfo().getHostname() + " Someone died, curr leader is: " + leaderName);

    }
}
