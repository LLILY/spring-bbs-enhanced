package app.redis;

import app.Cache;
import app.CacheConfigure;
import app.main.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Set;

/**
 * Created by lili19289 on 2016/8/29.
 */
public class RedisCacheImpl  implements Cache {



    protected JedisPool jedisPool;

    private static RedisCacheImpl redisCacheImpl;

    public static RedisCacheImpl getInstance(){
        if(redisCacheImpl==null){
            redisCacheImpl = new RedisCacheImpl();
            redisCacheImpl.initJedisPool();
        }
        return  redisCacheImpl;
    }


    /**
     * 获取连接池.
     *
     * @return 连接池实例
     */
    private Jedis getJedisPool() {
        if (null == jedisPool) {
            throw new IllegalStateException("Please call init method first.");
        }
        return jedisPool.getResource();
    }

    /**
     * 如果你遇到 java.net.SocketTimeoutException: Read timed out
     * exception的异常信息 请尝试在构造JedisPool的时候设置自己的超时值.
     * JedisPool默认的超时时间是2秒(单位毫秒)
     */
    public JedisPool initJedisPool(){
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(CacheConfigure.MAX_IDLE);
        config.setMaxWaitMillis(CacheConfigure.MAX_WAIT);
        config.setMaxTotal(CacheConfigure.DEFAULT_MAX_TOTAL);
        config.setMinIdle(CacheConfigure.DEFAULT_MIN_IDLE);
        config.setMinEvictableIdleTimeMillis(CacheConfigure.DEFAULT_EVICTABLE_IDLE_TIME_MILLIS);
        config.setTestOnBorrow(true);//当调用borrow Object方法时，是否进行有效性检查
        config.setTestOnReturn(true);//#当调用return Object方法时，是否进行有效性检查
        try {
            jedisPool = new JedisPool(config, CacheConfigure.ADDR, CacheConfigure.PORT, CacheConfigure.TIMEOUT);
        } catch (Exception e) {
            e.printStackTrace();
            Logger.error(" redis init failed -------",e);
        }
        return jedisPool;
    }

    public void destory() {
        if (null != jedisPool) {
            jedisPool.close();
        }
    }






    public void set(String key, String value) {
        Jedis jedis = getJedisPool();
        try {
            jedis.set(key, value);
        } finally {
            jedis.close();
        }
    }

    public void set(String key, String value, int expireSecs) {
        Jedis jedis = getJedisPool();
        try {
            jedis.setex(key,expireSecs,value);
        } finally {
            jedis.close();
        }
    }

    public String get(String key) {
        Jedis jedis = getJedisPool();
        try {
            return jedis.get(key);
        } finally {
            jedis.close();
        }
    }

    public boolean expire(String key, int expireSecs) {
        Jedis jedis = getJedisPool();
        try {
            return 1 == jedis.expire(key, expireSecs);
        } finally {
            jedis.close();
        }
    }

    public boolean persist(String key) {
        Jedis jedis = getJedisPool();
        try {
            return 1 == jedis.persist(key);
        } finally {
            jedis.close();
        }
    }


    public boolean delete(String key) {
        Jedis jedis = getJedisPool();
        try {
            return 1 == jedis.del((key));
        } finally {
            jedis.close();
        }
    }


    public boolean exists(String key) {
        Jedis jedis = getJedisPool();
        try {
            return jedis.exists((key));
        } finally {
            jedis.close();
        }
    }


    public long incrBy(String key, long by) {
        Jedis jedis = getJedisPool();
        try {
            return jedis.incrBy((key), by);
        } finally {
            jedis.close();
        }
    }


    public long incr(String key) {
        Jedis jedis = getJedisPool();
        try {
            return jedis.incr((key));
        } finally {
            jedis.close();
        }
    }


    public long decrBy(String key, long by) {
        Jedis jedis = getJedisPool();
        try {
            return jedis.decrBy((key), by);
        } finally {
            jedis.close();
        }
    }


    public long decr(String key) {
        Jedis jedis = getJedisPool();
        try {
            return jedis.decr(key);
        } finally {
            jedis.close();
        }
    }


    public boolean del(String... keys) {
        Jedis jedis = getJedisPool();
        try {
            return 0 != jedis.del(keys);
        } finally {
            jedis.close();
        }
    }

    public Long sdd(String key, String... members) {

        Jedis jedis = getJedisPool();
        try {
            return jedis.sadd(key, members);
        } finally {
            jedis.close();
        }
    }

    public Long scard(String key) {

        Jedis jedis = getJedisPool();
        try {
            return jedis.scard(key);
        } finally {
            jedis.close();
        }
    }

    public Long srem(String key, String... members) {

        Jedis jedis = getJedisPool();
        try {
            return jedis.srem(key, members);
        } finally {
            jedis.close();
        }
    }

    public Set<String> smembers(String key) {

        Jedis jedis = getJedisPool();
        try {
            return jedis.smembers(key);
        } finally {
            jedis.close();
        }
    }

    public boolean sismember(String key, String member) {

        Jedis jedis = getJedisPool();
        try {
            return jedis.sismember(key, member);
        } finally {
            jedis.close();
        }
    }

    public Long zadd(String key, double score, String member) {
        Jedis jedis = getJedisPool();
        try {
            return jedis.zadd(key, score,member);
        } finally {
            jedis.close();
        }
    }

    public Long zcard(String key) {

        Jedis jedis = getJedisPool();
        try {
            return jedis.zcard(key);
        } finally {
            jedis.close();
        }
    }

    public Set<String> zrange(String key, long start, long end) {

        Jedis jedis = getJedisPool();
        try {
            return jedis.zrange(key,start,end);
        } finally {
            jedis.close();
        }
    }

    public Long ttl(String key){
        Jedis jedis = getJedisPool();
        try {
            return jedis.ttl(key);
        } finally {
            jedis.close();
        }
    }


    public Long setnx(String key){
        Jedis jedis = getJedisPool();
        try {
            return jedis.setnx(key,"0");
        } finally {
            jedis.close();
        }
    }

    public Long setnx(String key, String value, int expireTime) {
        Jedis jedis = getJedisPool();
        try {
            Long ret = jedis.setnx(key, value);
            jedis.expire(key, expireTime);
            return ret;
        } finally {
            jedis.close();
        }
    }
}
