package app.zkConfigure;

import app.curator.ZooKeeperTreeCacheListener;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.log4j.Logger;

/**
 * Created by lili19289 on 2016/8/24.
 */
public class ZooKeeperConfigureListener implements TreeCacheListener {

    private static final Logger LOG = Logger.getLogger(ZooKeeperConfigureListener.class);

    public void childEvent(CuratorFramework client, TreeCacheEvent event) {
        ChildData data = event.getData();
        try {
            if (data != null) {
                switch (event.getType()) {
                    case NODE_ADDED:
                        System.out.println("treeCache ----- NODE_ADDED : " + data.getPath() + "  数据:" + new String(data.getData()));
                        break;
                    case NODE_REMOVED:
                        System.out.println("treeCache -----  NODE_REMOVED : " + data.getPath() + "  数据:" + new String(data.getData()));
                        break;
                    case NODE_UPDATED:
                        System.out.println("treeCache -----  NODE_UPDATED : " + data.getPath() + "  数据:" + new String(data.getData()));
                        break;
                    default:
                        break;
                }
            } else {
                System.out.println("data is null : " + event.getType());
            }
        }
        catch(Exception e) {
            LOG.error("ZooKeeperTreeCacheListener listen failed,the path is "+data.getPath() ,e);
        }
    }
}
