package app.utils;

import app.main.Logger;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by lili19289 on 2016/8/23.
 */
public class PropertiesUtil {


    //填写相对路径，class文件下的路径
    public static Properties getFromFile(String filePath) {
        filePath = EnvironmentUtil.getClassPath() + filePath;
        return readFile(new File(filePath));
    }

    //填写绝对路径，硬盘上的文件
    public static Properties readFromFile(String filePath) {
        File file = new File(filePath);
        return readFile(file);
    }

    public static Properties readFile(File file){
        Properties prop = null;
        InputStream ins = null;
        try {
            ins = new FileInputStream(file);
            prop = new Properties();
            prop.load(ins);
            return prop;
        } catch (FileNotFoundException e) {
            Logger.error("Can not find the file, it may result working problem. path: " , e);
        } catch (Exception e) {
            Logger.error("init configure properties failed", e);
        } finally {
            IOUtils.closeQuietly(ins);
        }
        return null;
    }

}
