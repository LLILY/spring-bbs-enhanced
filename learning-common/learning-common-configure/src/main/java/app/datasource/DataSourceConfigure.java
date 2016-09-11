package app.datasource;

import app.context.RuntimeContext;
import app.utils.JackSonUtil;
import app.utils.PropertiesUtil;
import app.utils.ValueUtil;
import app.zookeeper.ConfigureUtil;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool.impl.GenericKeyedObjectPool;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Created by lili19289 on 2016/8/23.
 */
public class DataSourceConfigure extends BasicDataSource{

    public static final String PREFFIX = "db.";

    public static final String PROP_KEY_DRIVERCLASSNAME =PREFFIX+ "driver";

    public static final String PROP_KEY_URL = PREFFIX+"url";

    public static final String PROP_KEY_USERNAME =PREFFIX+ "username";

    public static final String PROP_KEY_PASSWORD = PREFFIX+"password";

    public static final String PROP_KEY_MAXACTIVE = PREFFIX+"maxActive";

    public static final String PROP_KEY_INITIALSIZE = PREFFIX+"initialSize";

    public static final String PROP_KEY_MAXWAIT =PREFFIX+ "maxWait";

    public static final String PROP_KEY_MAXIDLE = PREFFIX+"maxIdle";

    public static final String PROP_KEY_MINIDLE = PREFFIX+"minIdle";

    public static final String PROP_KEY_TIMEBETWEENEVICTIONRUNSMILLS = PREFFIX+"timeBetweenEvictionRunsMillis";

    public static final String PROP_KEY_MINEVICTABLEIDLETIMEMILLIS = PREFFIX+"minEvictableIdleTimeMillis";





    private static final Log LOG = LogFactory.getLog(DataSourceConfigure.class);

    private static Properties prop;

    public Connection getConnection() throws SQLException {
        return this.getDataSource().getConnection();
    }

    public DataSource getDataSource (){
        initDataSource();
        try {
            this.dataSource= this.createDataSource();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this.dataSource;
    }

    public void initDataSource(){
        Map<String,Object > configureData = ConfigureUtil.loadConfigData("db");
        System.err.println(configureData.get(PROP_KEY_DRIVERCLASSNAME).toString());
        this.setDriverClassName(configureData.get(PROP_KEY_DRIVERCLASSNAME).toString());
        this.setUrl(configureData.get(PROP_KEY_URL).toString());
        this.setUsername(configureData.get(PROP_KEY_USERNAME).toString());
        this.setPassword(configureData.get(PROP_KEY_PASSWORD).toString());
        int initialSize = ValueUtil.getInt(configureData.get(PROP_KEY_INITIALSIZE), -1);
        if (-1 == initialSize) {
            initialSize = this.initialSize;
        }
        this.setInitialSize(initialSize);

        // maxActive
        int maxActive = ValueUtil.getInt(configureData.get(PROP_KEY_MAXACTIVE), -1);
        if (-1 == maxActive) {
            maxActive = this.maxActive;
        }
        this.setMaxActive(maxActive);

        // maxIdle
        int maxIdle = ValueUtil.getInt(configureData.get(PROP_KEY_MAXIDLE), -1);
        if (-1 == maxIdle) {
            maxIdle = this.maxIdle;
        }
        this.setMaxIdle(maxIdle);

        // minIdle
        int minIdle = ValueUtil.getInt(configureData.get(PROP_KEY_MINIDLE), -1);
        if (-1 == minIdle) {
            minIdle = this.minIdle;
        }
        this.setMinIdle(minIdle);

        // maxWait
        long maxWait = ValueUtil.getLong(configureData.get(PROP_KEY_MAXWAIT), -1);
        if (-1 == maxWait) {
            maxWait = this.maxWait;
        }
        this.setMaxWait(maxWait);

        // timeBetweenEvictionRunsMillis
        long timeBetweenEvictionRunsMillis = ValueUtil.getLong(configureData.get(PROP_KEY_TIMEBETWEENEVICTIONRUNSMILLS),
                -1);
        if (-1 == timeBetweenEvictionRunsMillis) {
            timeBetweenEvictionRunsMillis = this.timeBetweenEvictionRunsMillis;
        }
        this.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);

        // minEvictableIdleTimeMillis
        long minEvictableIdleTimeMillis = ValueUtil.getLong(configureData.get(PROP_KEY_MINEVICTABLEIDLETIMEMILLIS), -1);
        if (-1 == minEvictableIdleTimeMillis) {
            minEvictableIdleTimeMillis = this.minEvictableIdleTimeMillis;
        }
        this.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
    }


    public static void changeDataSource(){
       DataSourceConfigure dataSourceConfigure = (DataSourceConfigure) RuntimeContext.getBean("dataSource");
        try {
            dataSourceConfigure.close();
            dataSourceConfigure.initDataSource();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
}
