package app.utils;

import app.zookeeper.ConfigChangeSubscriber;
import app.zookeeper.ZkUtils;
import org.I0Itec.zkclient.ZkClient;

/**
 * Created by lili19289 on 2016/8/15.
 */
public class ZookeeperUtil {

    private static ZkClient zkClient;
    private static ConfigChangeSubscriber zkConfig;


    public static void initZk(){
        System.out.println("********************************************");
        try {
            zkClient = new ZkClient("127.0.0.1:2181");
            ZkUtils.mkPaths(zkClient, "/zkSample/conf");
            if (!zkClient.exists("/zkSample/conf/test1.properties"))
                zkClient.createPersistent("/zkSample/conf/test1.properties");
            if (!zkClient.exists("/zkSample/conf/test2.properties"))
                zkClient.createPersistent("/zkSample/conf/test2.properties");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void writeData(){
        zkClient.writeData("/zkSample/conf/test1.properties", "aa=1");
    }
}
