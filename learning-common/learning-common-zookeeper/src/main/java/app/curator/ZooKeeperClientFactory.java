package app.curator;

import app.main.Configure;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * Created by lili19289 on 2016/8/19.
 */
public class ZooKeeperClientFactory {

    private static final Logger LOG = Logger.getLogger(ZooKeeperClientFactory.class);

    public static final String ZOOKEEPER_CONNECTSTRING = Configure.configuration.getProperty("zookeeper.connectString","localhost:2181");
    public static final String ZOOKEEPER_BASE_PATH = Configure.configuration.getProperty("zookeeper.basePath","/root");

    public static ZooKeeperClient createZooKeeperClient() {
        try {
            if (StringUtils.isNotBlank(ZOOKEEPER_CONNECTSTRING)) {
                if (StringUtils.isNotBlank(ZOOKEEPER_BASE_PATH)) {
                    ZooKeeperClient zkClient = new ZooKeeperClient(ZOOKEEPER_CONNECTSTRING);
                    zkClient.start();
                    zkClient.mkdirs(ZOOKEEPER_BASE_PATH);
                    zkClient.close();
                }
                ZooKeeperClient zkClient = new ZooKeeperClient( ZOOKEEPER_CONNECTSTRING + ZOOKEEPER_BASE_PATH);
                LOG.info("create ZooKeeperClient of: " + ZOOKEEPER_CONNECTSTRING);
                zkClient.start();
                return zkClient;
            }
        } catch (Exception e) {
            LOG.error(ZOOKEEPER_CONNECTSTRING + " not configured yet, create ZooKeeperClient failed!!!");

        }
        return null;
    }

}
