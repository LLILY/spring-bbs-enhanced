package app.curator;

import app.context.RuntimeContext;
import org.apache.log4j.Logger;

/**
 * Created by lili19289 on 2016/8/19.
 */
public class ZooKeeperLoader {
    private static final Logger LOG = Logger.getLogger(ZooKeeperLoader.class);

    ZooKeeperClient zooKeeperClient;
    public void init(){
        try {
            zooKeeperClient =  RuntimeContext.getBean(ZooKeeperClient.class);
            zooKeeperClient.addNodeCacheListener("/learning",ZooKeeperNodeCacheListener.class);
//            zooKeeperClient.addPathChildrenListener("/learning",new ZooKeeperPathChildrenCacheListener());
//            zooKeeperClient.addTreeCacheListener("/learning",new ZooKeeperTreeCacheListener());
//            zooKeeperClient.addNodeCacheListener("/learning");
        }catch (Exception e){
            e.printStackTrace();
            LOG.error("ZooKeeperLoader connect zookeeper failed ",e);
        }

    }

}
