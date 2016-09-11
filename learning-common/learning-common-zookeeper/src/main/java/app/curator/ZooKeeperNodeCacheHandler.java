package app.curator;

import app.utils.IoUtil;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.log4j.Logger;
import org.eclipse.jetty.util.ConcurrentHashSet;


import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by lili19289 on 2016/8/19.
 * 监视一个结点的创建、更新、删除，并将结点的数据缓存在本地。
 * NodeCache主要用来监听节点本身的变化,当节点的状态发生变更后,会回调NodeCachaListener
 * 这个类为了解决一个节点有多个监听器的操作
 */
public class ZooKeeperNodeCacheHandler implements NodeCacheListener {

    private Logger LOG = Logger.getLogger(ZooKeeperNodeCacheHandler.class);

    private String path;

    protected NodeCache nodeCache;

    protected Set<NodeCahceChangeListener> watchListeners = new ConcurrentHashSet<NodeCahceChangeListener>();

    protected  ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
    private boolean started;

    public ZooKeeperNodeCacheHandler(String path, NodeCache nodeCache){
        this.path=path;
        this.nodeCache = nodeCache;
        this.nodeCache.getListenable().addListener(this);
        if (LOG.isDebugEnabled()) {
            LOG.debug("NodeCacheHandler created, path: " + path);
        }

    }

    public String getStringData() {
        byte[] bytes = getData();
        String data = null == bytes ? null : new String(bytes, IoUtil.CHARSET_UTF8);
        return data;
    }

    public byte[] getData() {
        ChildData dataObj = nodeCache.getCurrentData();
        return null == dataObj ? null : dataObj.getData();
    }

    public void startWatch() {
        if (!started) {
            try {
                nodeCache.start(true);
                started = true;
            } catch (Exception e) {
                LOG.error("start watch failed", e);
            }
        }
    }

    public void stopWatch() {
        if (started) {
            try {
                nodeCache.close();
                started = false;
            } catch (IOException e) {
                LOG.error("stop watch failed", e);
            }
        }
    }

    public void addListeners(NodeCahceChangeListener... listeners){
        if(listeners!=null && listeners.length>0 ){
            byte[] data = getData();
            for(NodeCahceChangeListener listener : listeners){
                if(watchListeners.add(listener)){
                    if (LOG.isInfoEnabled()) {
                        LOG.info(listener + " added to nodeWatching " + path);
                    }
                    listener.nodeChanged(path, data);
                }
            }
        }
    }

    public void removeListener(NodeCahceChangeListener... listeners) {
        if (null != listeners && 0 < listeners.length) {
            for (NodeCahceChangeListener listener : listeners) {
                if (LOG.isInfoEnabled()) {
                    LOG.info(listener + " removed from nodeWatching " + path);
                }
                watchListeners.remove(listener);
            }
        }
    }

    public void nodeChanged() throws Exception {
        if(watchListeners.isEmpty()){
            return ;
        }
        byte[] data = getData();
        cachedThreadPool.submit((Runnable)() ->{
            try{
                for(NodeCahceChangeListener listener :watchListeners){
                    listener.nodeChanged(path,data);
                }
            }catch (Exception e){
                LOG.error("notify nodeChanged failed", e);
            }

        });

        System.out.println("NodeCache changed, data is: " + new String(nodeCache.getCurrentData().getData()));
    }


    public static  interface NodeCahceChangeListener{
        public void nodeChanged(String path,byte[] data);
    }


}
