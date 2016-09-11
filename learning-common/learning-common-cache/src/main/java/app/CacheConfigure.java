package app;

import app.zookeeper.ConfigureUtil;

import java.util.Map;

/**
 * Created by lili19289 on 2016/8/30.
 */
public class CacheConfigure {

    public static int MAX_ACTIVE = 1024;
    public static int MAX_IDLE = 200;
    public static int MAX_WAIT = 100000;
    public static int TIMEOUT = 10000;
    public static final int DEFAULT_MIN_IDLE = 1;
    public static final long DEFAULT_EVICTABLE_IDLE_TIME_MILLIS = 60 * 1000L;
    public static final int DEFAULT_MAX_TOTAL = 4;
    public static final int DEFAULT_MAX_IDLE = 1;
    public static String ADDR ;
    public static int PORT ;

    public static void loadConfigure(){
        Map<String,Object > configureData = ConfigureUtil.loadConfigData("cache");
        ADDR = (String) configureData.get("cache.address");
        PORT=Integer.parseInt(configureData.get("cache.port").toString());
    }


}
