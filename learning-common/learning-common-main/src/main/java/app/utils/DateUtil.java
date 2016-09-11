package app.utils;

import app.main.Configure;
import com.sun.imageio.plugins.common.I18N;
import org.apache.commons.codec.language.bm.Lang;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by lili19289 on 2016/8/15.
 */
public class DateUtil {


    public static String format(Date date) {
        return format(date, Configure.getDateFormat());
    }
    public static String format(Long time) {
        return format(new Date(time));
    }

    public static String format(Long time, String pattern) {
        return format(new Date(time),pattern);
    }

    public static String format(Date date, String pattern) {
        return new SimpleDateFormat(pattern) .format(date);
    }

    public static String format(Date date, String pattern,
                                String timezone) {
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        df.setTimeZone(TimeZone.getTimeZone(timezone));
        return df.format(date);
    }

    public static Integer page(Number number, Integer pageSize) {
        return Integer.valueOf(number.intValue() / pageSize.intValue()
                + (number.intValue() % pageSize.intValue() > 0 ? 1 : 0));
    }


    public static String getPassedTime(Date date) {
        Date now = new Date();
        if (now.before(date)) {
            return "";
        }
        long delta = (now.getTime() - date.getTime()) / 1000;
        if (delta < 60) {
            return delta + "秒前";
        }
        if (delta < 60 * 60) {
            long minutes = delta / 60;
            return minutes + "分钟前";
        }
        if (delta < 24 * 60 * 60) {
            long hours = delta / (60 * 60);
            return hours + "小时前";
        }
        if (delta < 4 * 24 * 60 * 60) {
            long days = delta / (24 * 60 * 60);
            return days + "天前";
        }
        return format(date, "yyyy-MM-dd");
    }

    public static String getPassedTime(long nowTime) {
        return getPassedTime(new Date(nowTime));
    }


}
