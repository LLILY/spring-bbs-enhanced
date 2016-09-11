package app.zookeeper;

import org.I0Itec.zkclient.ZkClient;
import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by lili19289 on 2016/8/13.
 */
public class ZookeeperConfigLoader {

    private static final Logger LOG = Logger.getLogger(ZookeeperConfigLoader.class);

    private ZkClient zkClient;
    private ConfigChangeSubscriber zkConfig;

    public void init(){
        try {
//            this.zkClient =  new ZkClient("127.0.0.1:2181");
//            this.zkConfig = new ZkConfigChangeSubscriberImpl(zkClient,"/zkSample/conf");
//            this.zkConfig.subscribe("test1.properties", new ConfigChangeListenerImpl());
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
