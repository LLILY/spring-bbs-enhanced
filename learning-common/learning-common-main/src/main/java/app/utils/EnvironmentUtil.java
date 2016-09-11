package app.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.File;

/**
 * Created by lili19289 on 2016/8/15.
 */
public class EnvironmentUtil {
    private static final String RUNTIME_CONFIG_ROOT_ENV_KEY = "RUNTIME_CONFIG_ROOT";
    private static final String RUNTIME_CONFIG_ROOT_DIR_NAME = "zk_config_root";

    public static boolean isOnWindows() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.indexOf("win") >= 0;
    }

    public static String getClassPath(){
        return  Thread.currentThread().getContextClassLoader().getResource("").getPath();
    }

    public static String getSystemPath(){
        return System.getProperty("user.dir");
    }

    /**
     * 获取系统运行期配置文件路径
     * @return
     */
    public static String getRuntimeConfigPath() {
        String path = System.getenv().get(RUNTIME_CONFIG_ROOT_ENV_KEY);
        if (StringUtils.isBlank(path)) {
            if (isOnWindows()) {
                path = "C:/"+  RUNTIME_CONFIG_ROOT_DIR_NAME;
            } else {
                path = '/' + RUNTIME_CONFIG_ROOT_DIR_NAME;
            }
        }
        return path;
    }
}
