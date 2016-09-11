package app.zookeeper;

import app.utils.EnvironmentUtil;
import app.utils.JackSonUtil;
import app.utils.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by lili19289 on 2016/8/24.
 */
public class ConfigureUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigureUtil.class);


    public static Map<String, Object> loadConfigData(String catatlog) {
        Properties properties=null;
        Map<String, Object> configData = new HashMap<String, Object>();
        String filePath=EnvironmentUtil.getRuntimeConfigPath()+"/"+catatlog+"/"+catatlog+".properties";
        properties = PropertiesUtil.readFromFile(filePath);
        if (null == properties || properties.isEmpty()) {
            properties = PropertiesUtil.getFromFile(ConfigureLoader.CONFIGURE_PATH+"/"+catatlog+".properties");
            LOGGER.info("use configure data in project");
        } else {
            LOGGER.info("use configure data in local data file");
        }
        configData = JackSonUtil.fromJson(JackSonUtil.toJson(properties),Map.class);
        return configData;
    }

    public static  Map<String, Object> getConfigureData(String catatlog) {
        Map<String, Object> configData = ConfigureLoader.getConfigDataFromZooKeeper(catatlog);
        if(configData!=null && configData.isEmpty()){
            configData = loadConfigData(catatlog);
        }
        return configData;
    }

    public static  void setConfigureData(String catatlog,Map<String,Object> map) {
        ConfigureLoader.setConfigDataFromZooKeeper(catatlog,JackSonUtil.toJson(map) );
        StringBuilder sb = new StringBuilder();
        for(Map.Entry entry : map.entrySet()){
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append("\r\n");
        }
        File file  = new File(EnvironmentUtil.getRuntimeConfigPath()+"/"+catatlog+"/"+catatlog+".properties");
        try{
            writeToFile(file,sb.toString());
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public static Map<Object, Object> readOriDataFromFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return Collections.emptyMap();
        }
        String content = readFromFile(file);
        if (null == content) {
            return Collections.emptyMap();
        }
        Map<Object, Object> data = new HashMap<Object, Object>();

        return data;
    }


    private static String readFromFile(File file) {
        if (!file.exists()) {
            return null;
        }
        FileInputStream fis = null;
        InputStreamReader isr = null;
        StringBuilder sBuilder = new StringBuilder(4096);
        char[] buffer = new char[4096];
        int len = 0;
        try {
            fis = new FileInputStream(file);
            isr = new InputStreamReader(fis, "UTF-8");
            while (-1 != (len = isr.read(buffer))) {
                sBuilder.append(buffer, 0, len);
            }
            return sBuilder.toString();
        } catch (Exception e) {
            LOGGER.error("read from file failed: " + file.getAbsolutePath(), e);
            return null;
        } finally {
            close(isr);
            close(fis);
        }
    }

    private static void writeToFile(File file, String content) throws IOException {
        File dir = file.getParentFile();
        if (!dir.isDirectory() && !dir.mkdirs()) {
            throw new IOException("create dir failed: " + dir.getAbsolutePath());
        }
        FileOutputStream fos = null;
        OutputStreamWriter osw = null;
        try {
            fos = new FileOutputStream(file);
            osw = new OutputStreamWriter(fos, "UTF-8");
            osw.write(content);
            osw.flush();
        } finally {
            close(osw);
            close(fos);
        }
    }

    private static void close(Closeable io) {
        if (null != io) {
            try {
                io.close();
            } catch (Exception e) {
                LOGGER.error("close io failed", e);
            }
        }
    }

    private static Map<String, String> transToUnicodeMap(Map<String, String> params) {
        Map<String, String> newParams = new HashMap<String, String>();
        for (Map.Entry<String, String> paramEntry : params.entrySet()) {
//            newParams.put(paramEntry.getKey(), StringUtil.toPropertiesValue(paramEntry.getValue()));
        }
        return newParams;
    }


}
