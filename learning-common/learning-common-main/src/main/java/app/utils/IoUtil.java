package app.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Created by lili19289 on 2016/8/19.
 */
public class IoUtil {
    private static final Log LOGGER = LogFactory.getLog(IoUtil.class);

    public static final Charset CHARSET_UTF8 = Charset.forName("UTF-8");


    public static void close(Closeable closeable) {
        if (null != closeable) {
            try {
                closeable.close();
            } catch (IOException e) {
                LOGGER.error("Close closeable faild: " + closeable.getClass(), e);
            }
        }
    }
}
