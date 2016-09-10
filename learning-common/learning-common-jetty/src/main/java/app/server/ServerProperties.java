package app.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by lili19289 on 2016/8/6.
 */
public class ServerProperties {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerProperties.class);


    private static final String FILE_NAME = "server.properties";

    private static Properties prop;

    static {
        init();
    }

    private static void init() {
        String filePath =getClassPath()+"/conf/server.properties";
        InputStream ins = null;
        try {
             ins  = new FileInputStream(filePath);
            prop = new Properties();
            prop.load(ins);
        } catch (FileNotFoundException e) {
            LOGGER.error("Can not find server properties, it may result working problem. path: " + filePath, e);
        } catch (Exception e) {
            LOGGER.error("init server properties failed", e);
        } finally {
            if (null != ins) {
                try {
                    ins.close();
                } catch (IOException e) {
                    LOGGER.error("Close closeable faild: " + ins.getClass(), e);
                }
            }
        }
    }

    public static String getClassPath(){
        return  Thread.currentThread().getContextClassLoader().getResource("").getPath();
    }

    public static String getProperty(String key) {
        if (null != prop) {
            return prop.getProperty(key);
        }
        return null;
    }
}
