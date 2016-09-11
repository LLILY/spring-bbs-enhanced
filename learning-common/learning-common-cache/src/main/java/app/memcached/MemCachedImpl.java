package app.memcached;

import app.Cache;
import app.main.Configure;
import app.main.Logger;
import net.spy.memcached.*;
import net.spy.memcached.auth.AuthDescriptor;
import net.spy.memcached.auth.PlainCallbackHandler;
import net.spy.memcached.internal.BulkFuture;
import net.spy.memcached.internal.GetFuture;
import net.spy.memcached.internal.OperationFuture;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by lili19289 on 2016/8/29.
 */
public class MemCachedImpl implements Cache {

    private static MemCachedImpl memcachedImpl;
    MemcachedClient client;

    public static MemCachedImpl getInstance()  {
        if(memcachedImpl==null){
            memcachedImpl = new MemCachedImpl();
            memcachedImpl.initClient();
        }
        return memcachedImpl;
    }



    public void initClient() {
        List addrs;
        String memcachePassword;
        if (Configure.configuration.containsKey("memcached.host")) {
            addrs = AddrUtil.getAddresses(Configure.configuration
                    .getProperty("memcached.host"));
        } else {
            if (!Configure.configuration.containsKey("memcached.1.host")) {
                Logger.error("Bad configuration for memcached: missing host(s)");
            }
            int memcacheUser = 1;
            for (memcachePassword = ""; Configure.configuration
                    .containsKey("memcached." + memcacheUser + ".host"); ++memcacheUser) {
                memcachePassword = memcachePassword
                        + Configure.configuration.get("memcached." + memcacheUser
                        + ".host") + " ";
            }
            addrs = AddrUtil.getAddresses(memcachePassword);
        }
        try{
            if (Configure.configuration.containsKey("memcached.user")) {
                String arg5 = Configure.configuration.getProperty("memcached.user");
                memcachePassword = Configure.configuration
                        .getProperty("memcached.password");
                if (memcachePassword == null) {
                    Logger.error("Bad configuration for memcached: missing password");
                }

                AuthDescriptor ad = new AuthDescriptor(new String[]{"PLAIN"},
                        new PlainCallbackHandler(arg5, memcachePassword));
                ConnectionFactory cf = (new ConnectionFactoryBuilder())
                        .setProtocol(ConnectionFactoryBuilder.Protocol.BINARY).setAuthDescriptor(ad).build();
                this.client = new MemcachedClient(cf, addrs);
            } else {
                this.client = new MemcachedClient(addrs);
            }
        }catch (Exception e){
            Logger.error(" memcache connect failed",e);
        }

    }


    public void destory() {
        if (null != client) {
            client.shutdown();
        }
    }




    public void set(String key, String value) {
        set(key,value,0);
    }

    public void set(String key, String value, int expireSecs) {
        this.client.set(key,expireSecs,value);
    }

    public Object getObject(String key) {
        GetFuture future = this.client.asyncGet(key);

        try {
            return future.get(1L, TimeUnit.SECONDS);
        } catch (Exception arg3) {
            future.cancel(false);
            return null;
        }
    }

    public String get(String key) {
        Object value = getObject(key);
        return String.valueOf(value);
    }

    public Map<String, Object> get(String[] keys) {
        BulkFuture future = this.client.asyncGetBulk(keys);
        try {
            return (Map) future.get(1L, TimeUnit.SECONDS);
        } catch (Exception arg4) {
            future.cancel(false);
            return Collections.emptyMap();
        }
    }

    protected boolean touch(String key, int expireSecs) {
        OperationFuture<Boolean> future = client.touch(key, expireSecs);
        return getFutureValue(future, false, key);
    }

    protected CASValue<Object> gets(String key) {
        OperationFuture<CASValue<Object>> future = client.asyncGets(key);
        return getFutureValue(future, null, key);
    }

    protected CASResponse cas(String key, Object value, int expireSecs, long casId) {
        OperationFuture<CASResponse> future = client.asyncCAS(key, casId, expireSecs, value);
        return getFutureValue(future, null, key);
    }

    public boolean expire(String key, int expireSecs) {
            CASValue<Object> value = gets(key);
            if (null != value) {
                CASResponse resp = cas(key, value.getValue(), expireSecs, value.getCas());
                return null != resp && CASResponse.OK == resp;
            }
            return false;

    }

    public boolean persist(String key) {
        return expire(key, 0);
    }

    public boolean delete(String key) {
        OperationFuture<Boolean> future = client.delete(key);
        return getFutureValue(future, false, key);
    }

    public boolean exists(String key) {
        return false;
    }

    public long incrBy(String key, long by) {
        return this.client.incr(key,by);
    }

    public long incr(String key) {
        return 0;
    }

    public long decrBy(String key, long by) {
        return 0;
    }

    public long decr(String key) {
        return 0;
    }

    public Long zadd(String key, double expire, String member) {
        return null;
    }

    public Long zcard(String key) {
        return null;
    }

    public Set<String> zrange(String key, long start, long end) {
        return null;
    }

    public Long ttl(String key) {
        return null;
    }

    public Long setnx(String key) {
        return null;
    }

    public Long setnx(String key, String value, int expireTime) {
        return null;
    }


    protected <V> V getFutureValue(Future<V> future, V defaultValue, String key) {
        try {
            return future.get(1L,  TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            Logger.error( " failed, key=" + key, e);
            future.cancel(false);
            return defaultValue;
        }
    }
}
