package app.curator;

import org.apache.commons.lang.StringUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.eclipse.jetty.util.ConcurrentHashSet;


import java.lang.reflect.Constructor;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Created by lili19289 on 2016/8/18.
 */
public class ZooKeeperClient {

    private static final Logger LOG = Logger.getLogger(ZooKeeperClient.class);

    public static final char PATH_SEPARATOR_CHAR = '/';

    public static final int DEFAULT_BASE_SLEEP_TIME_MS = 1000;

    public static final int DEFAULT_MAX_RETRIES = 0;

    private ConnectionState connectionState;//连接状态

    //连接状态监听
    private Set<ZooKeeperClientConnectionStateListener> connectionStateListeners = new ConcurrentHashSet<ZooKeeperClientConnectionStateListener>();

    private Map<String,ZooKeeperNodeCacheHandler> nodeCacheHandlerMap = new HashMap();
    private CuratorFramework client;

    /**
     * 创建ZooKeeper客户端。
     * @param connectString 服务器连接串。
     * @param retryPolicy 重试策略。
     */
    public ZooKeeperClient(String connectString, RetryPolicy retryPolicy) {
        client = CuratorFrameworkFactory.newClient(connectString, retryPolicy);
    }

    /**
     * 创建ZooKeeper客户端。
     * @param connectString 服务器连接串。
     * @param baseSleepTimeMs 重试间隔时间。
     * @param maxRetries 最大重试次数。
     */
    public ZooKeeperClient(String connectString, int baseSleepTimeMs, int maxRetries) {
        client = CuratorFrameworkFactory.newClient(connectString, new ExponentialBackoffRetry(baseSleepTimeMs,
                maxRetries));
    }

    /**
     * 创建ZooKeeper客户端。
     * @param connectString 服务器连接串。
     */
    public ZooKeeperClient(String connectString) {
        try {
        client = CuratorFrameworkFactory.newClient(connectString, new ExponentialBackoffRetry(
                DEFAULT_BASE_SLEEP_TIME_MS, DEFAULT_MAX_RETRIES));
        }catch (Exception e){
            LOG.error("zookeeperclient connect failed ,the connectString is "+connectString,e);
        }
    }

    /**
     * 启动该ZooKeeper客户端。
     */
    public void start() {
        try {
            // 先确认状态，如果已经启动或关闭时调用start会抛异常
            if (CuratorFrameworkState.LATENT == client.getState()) {
                client.getConnectionStateListenable().addListener(new ClientConnectionStateListener());
                client.start();
            }
        }catch (Exception e){
            LOG.error("zookeeperclient start failed ",e);
        }

    }

    /**
     * 关闭该ZooKeeper客户端。
     */
    public void close() {
        client.close();
    }


    /**
     * 创建指定路径,永久路径
     * @param path 路径。
     */
    public void createPersistent(String path) {
        create(path, CreateMode.PERSISTENT);
    }

    /**
     * 创建指定路径,临时路径
     * @param path 路径。
     */
    public void createEphemeral(String path) {
        create(path, CreateMode.EPHEMERAL);
    }

    /**
     * 创建指定路径，如果父路径不存在则抛出异常。
     * @param path 路径。
     * @param createMode 要创建的路径的类型。
     */
    public void create(String path, CreateMode createMode) {
        // 先检查路径是否存在，如果已存在则不做操作
        if (!exists(path)) {
            try {
                client.create().creatingParentsIfNeeded()//如果指定的节点的父节点不存在，递归创建父节点
                        .withMode(createMode)//存储类型（临时的还是持久的）
                        .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)//访问权限
                        .forPath(path);
            } catch (Exception e) {
                String message = "create path failed, path=" + path;
                LOG.error(message, e);
                throw new RuntimeException(message, e);
            }
        }
    }


    /**
     * 删除指定路径，会递归删除其下子路径，如果路径不存在则不操作。
     * @param path 路径。
     */
    public void delete(String path) {
        // 先检查路径是否存在，如果不存在则不做操作
        if (exists(path)) {
            // 递归删除子路径
            delete(path, true);
        }
    }

    /**
     * 删除指定路径，如果路径不存在，则抛出异常。
     * @param path 路径。
     * @param clearChildren 如果为true，则递归删除子路径，否则只删除指定路径，如果路径非空，则抛出异常。
     */
    public void delete(String path, boolean clearChildren) {
        try {
            if (clearChildren) {
                // 列出所有子路径
                List<String> children = client.getChildren().forPath(path);
                if (!children.isEmpty()) {
                    // 遍历子路径操作
                    for (String childPath : children) {
                        // 递归调用删除子路径
                        delete(path + PATH_SEPARATOR_CHAR + childPath, true);
                    }
                }
            }
            // 删除本路径
            client.delete().forPath(path);
        } catch (Exception e) {
            String message = "delete path failed, path=" + path;
            LOG.error(message, e);
            throw new RuntimeException(message, e);
        }
    }

    /**
     * 检查指定路径是否存在。
     * @param path 路径。
     * @return 如果存在则返回true，否则返回false。
     */
    public boolean exists(String path) {
        try {
            // 如果返回的Stat不为null则表示该路径存在
            return null != client.checkExists().forPath(path);
        } catch (Exception e) {
            String message = "check exists failed, path=" + path;
            LOG.error(message, e);
            throw new RuntimeException(message, e);
        }
    }


    /**
     * 获取指定路径中的子路径名。
     * @param path 要获取子路径的父路径。
     * @return 子路径名，如果父路径不存在，则返回空白列表。
     */
    public List<String> getChildren(String path) {
        try {
            // 先检查路径是否存在，如果存在则获取子路径名
            if (exists(path)) {
                return client.getChildren().forPath(path);
            } else {// 如果该路径不存在则返回空列表
                return Collections.emptyList();
            }
        } catch (Exception e) {
            String message = "get children failed, path=" + path;
            LOG.error(message, e);
            throw new RuntimeException(message, e);
        }
    }

    public void mkdirs(String path) {
        mkdirs(path, CreateMode.PERSISTENT);
    }

    /**
     * 创建路径，如果父路径不存在，会自动创建。
     * @param path 路径。
     * @param createMode 要创建的路径的类型。
     */
    public void mkdirs(String path, CreateMode createMode) {
        // 先检查路径是否存在，如果已存在则不做操作
        if (!exists(path)) {
            try {
                client.create().creatingParentsIfNeeded().withMode(createMode).forPath(path);
            } catch (Exception e) {
                String message = "create path failed, path=" + path;
                LOG.error(message, e);
                throw new RuntimeException(message, e);
            }
        }
    }

    public  void setData( String path, String data) {
        try{
            if(!exists(path)){
                mkdirs(path);
            }
            if(StringUtils.isBlank(data)) client.setData().forPath(path);
            // set data for the given node
            client.setData().forPath(path,data.getBytes());
        }catch(Exception e){
            String message = "set data failed, path=" + path;
            LOG.error(message, e);
        }

    }

    /**
     * 获取指定路径上的数据。
     * @param path 路径。
     * @return 路径上的数据，如果路径不存在，则返回null。
     */
    public String getStringData(String path) {
        byte[] bytes = getData(path);
        return null == bytes ? null : new String(bytes, Charset.forName("UTF-8"));
    }
    /**
     * 获取指定路径上的数据。
     * @param path 路径。
     * @return 路径上的数据，如果路径不存在，则返回null。
     */
    public byte[] getData(String path) {
        try {
            // 先检查路径是否存在，如果存在则获取节点数据
            if (exists(path)) {
                byte[] bytes = client.getData().forPath(path);
                if (null == bytes) {
                    return null;
                }
                return bytes;
            } else {// 如果路径不存在则返回null
                return null;
            }
        } catch (Exception e) {
            String message = "get data failed, path=" + path;
            LOG.error(message, e);
            throw new RuntimeException(message, e);
        }
    }


    public CuratorFramework getCuratorFramework(){
        return client;
    }


    /**
     * node监听，一个监听方法
     * 该方法是直接实现NodeCacheListener来实现监听
     * @param path
     * @param listenerClass
     */
    public void addNodeCacheListener(String path,Class<? extends ZooKeeperNodeCacheListener> listenerClass){
        final NodeCache nodeCache = new NodeCache(client, path);
        Class[] paramTypes = {NodeCache.class};
        Object[] params = {nodeCache};
        Constructor con=null;
        ZooKeeperNodeCacheListener listener=null;
        try {
            con = listenerClass.getConstructor(paramTypes);
            listener =(ZooKeeperNodeCacheListener) con.newInstance(params);
            nodeCache.getListenable().addListener(listener);
            nodeCache.start(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    /**
     * node监听，一个或以上监听方法
     * 该方法是通过实现NodeCacheListener，然后对自己定义的NodeCahceChangeListener的接口进行调用来实现的监听
     * @param path
     * @param listeners
     */
    public void addNodeCacheListeners(String path, ZooKeeperNodeCacheHandler.NodeCahceChangeListener...listeners){
        ZooKeeperNodeCacheHandler nodeCacheListeners = getZooKeeperNodeCacheListeners(path);
        nodeCacheListeners.addListeners(listeners);
    }

    private ZooKeeperNodeCacheHandler getZooKeeperNodeCacheListeners(String path){
        ZooKeeperNodeCacheHandler nodeCacheListeners =nodeCacheHandlerMap.get(path);
        if(nodeCacheListeners==null){
            NodeCache nodeCache = new NodeCache(client,path);
            nodeCacheListeners = new ZooKeeperNodeCacheHandler(path,nodeCache);
            nodeCacheListeners.startWatch();
            nodeCacheHandlerMap.put(path,nodeCacheListeners);
        }
        return nodeCacheListeners;
    }





    /**
     * Path Cache：
     * 监视一个路径下
     * 1）孩子结点的创建、2）孩子结点的删除，3）以及孩子结点数据的更新。
     *  产生的事件会传递给注册的PathChildrenCacheListener。
     * @param path
     * @param listener
     */
    public void addPathChildrenListener(String path,PathChildrenCacheListener listener){
        if(!exists(path))mkdirs(path);
        try {
            PathChildrenCache childrenCache = new PathChildrenCache(client, path,true);
            childrenCache.getListenable().addListener(listener);
            childrenCache.start();
//            childrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("PathChildrenCache listen fail,path = "+path,e);
        }

    }

    /**
     *  监控 指定节点和节点下的所有的节点的变化--无限监听  可以进行本节点的删除(不在创建)
     * @param path
     * @param listener
     */
    public void addTreeCacheListener(String path, TreeCacheListener listener){
        if(!exists(path))mkdirs(path);
        try {
            TreeCache treeCache = new TreeCache(client,path);
            treeCache.getListenable().addListener(listener);
            treeCache.start();
        }catch (Exception e){
            LOG.error(" zookeeper treeCache listener failed ,the path is "+path,e);
        }
    }












    /*
    ********************************************************************************************************8
     */

    /**
     * 连接状态监听实现
     */
    private class ClientConnectionStateListener implements ConnectionStateListener {

        public void stateChanged(CuratorFramework client, ConnectionState newState) {
            connectionState = newState;
            notifyConnectionStateListener(newState);
        }

    }

    private void notifyConnectionStateListener(ConnectionState state) {
        if (state.isConnected()) {
            for (ZooKeeperClientConnectionStateListener listener : connectionStateListeners) {
                listener.notifyConnected();
            }
        } else {
            for (ZooKeeperClientConnectionStateListener listener : connectionStateListeners) {
                listener.notifyDisconnected();
            }
        }
    }

    private void notifyConnectionStateListener(ConnectionState state,
                                               ZooKeeperClientConnectionStateListener listener) {
        if (state.isConnected()) {
            listener.notifyConnected();
        } else {
            listener.notifyDisconnected();
        }
    }

    /**
     * 添加客户端连接状态监听
     * @param listener 客户端连接状态监听器
     */
    public void addClientConnectionStateListener(ZooKeeperClientConnectionStateListener listener) {
        // 任何情况下都添加，启动后才通知
        if (connectionStateListeners.add(listener) && CuratorFrameworkState.STARTED == client.getState()) {
            notifyConnectionStateListener(connectionState, listener);
        }
    }

}
