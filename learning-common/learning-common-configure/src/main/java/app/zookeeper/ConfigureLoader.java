package app.zookeeper;

import app.context.RuntimeContext;
import app.curator.ZooKeeperClient;
import app.curator.ZooKeeperTreeCacheListener;
import app.utils.JackSonUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;

/**
 * Created by lili19289 on 2016/8/24.
 */

public class ConfigureLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigureLoader.class);

    public static final String CONFIGURE_PATH="/configure";

    private static ZooKeeperClient client;

    public ConfigureLoader(){}

    public void initConfgure(){
        ZooKeeperClient zkClient = RuntimeContext.getBean(ZooKeeperClient.class);
        client=zkClient;
        client.addTreeCacheListener(CONFIGURE_PATH,new ZooKeeperConfigureListener());
    }

    public ConfigureLoader(ZooKeeperClient client){
        this.client=client;
    }


    public String getData(String path){
        return client.getStringData(path);
    }

    public void getData(String path,String data){
        client.setData(path,data);
    }


    public static Map<String, Object> getConfigDataFromZooKeeper(String category) {
        Map<String, Object> map = null;
        try{
            if (StringUtils.isNotBlank(category)) {
                map = JackSonUtil.fromJson(client.getStringData(CONFIGURE_PATH+"/"+category),Map.class);
            }
        } catch (Exception e){
            LOGGER.warn("no ZooKeeperClient exists, will not get data  from  zookeeper",e);
        }
       if(map==null){
           map= Collections.emptyMap();
       }
        return map;
    }

    public static void setConfigDataFromZooKeeper(String category,String data) {
        try{
            if (StringUtils.isNotBlank(category)) {
                client.setData(CONFIGURE_PATH+"/"+category,data);
            }
        } catch (Exception e){
            LOGGER.warn("no ZooKeeperClient exists, will not write to zookeeper",e);
        }
    }



}
