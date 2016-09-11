package app.curator;

import app.utils.IoUtil;
import org.apache.commons.io.IOUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.log4j.Logger;


/**
 * Created by lili19289 on 2016/8/20.
 * 监视一个路径下
 * 1）孩子结点的创建、2）孩子结点的删除，3）以及孩子结点数据的更新。
 * 不监听自己节点的操作
 * 产生的事件会传递给注册的PathChildrenCacheListener。
 */
public class ZooKeeperPathChildrenCacheListener implements PathChildrenCacheListener {
    private static final Logger LOG = Logger.getLogger(ZooKeeperPathChildrenCacheListener.class);
    @Override
    public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent)  {
        ChildData data = pathChildrenCacheEvent.getData();
        try{
            PathChildrenCacheEvent.Type eventType = pathChildrenCacheEvent.getType();
            switch (eventType) {
                case CHILD_ADDED:
                    System.out.println("CHILD_ADDED : "+ data.getPath() +"  数据:"+ new String(data.getData(), IoUtil.CHARSET_UTF8));
                    break;
                case CHILD_REMOVED:
                    System.out.println("CHILD_REMOVED : "+ data.getPath() +"  数据:"+ new String(data.getData(), IoUtil.CHARSET_UTF8));
                    break;
                case CHILD_UPDATED:
                    System.out.println("CHILD_UPDATED : "+ data.getPath() +"  数据:"+ new String(data.getData(), IoUtil.CHARSET_UTF8));
                    break;
                default:
                    break;
            }
        }catch (Exception e){
            LOG.error("ZooKeeperPathChildrenCacheListener listen failed,the path is "+data.getPath() ,e);
        }

    }
}

