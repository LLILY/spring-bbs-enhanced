package app.zkConfigure;

import app.curator.ZooKeeperClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by lili19289 on 2016/8/24.
 */
public class ZooKeeperConfigure {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZooKeeperConfigure.class);

    private static ZooKeeperClient client;

    public ZooKeeperConfigure(ZooKeeperClient client){
        this.client=client;
    }


    public String getData(String path){
        return client.getStringData(path);
    }

    public void getData(String path,String data){
         client.setData(path,data);
    }


}
