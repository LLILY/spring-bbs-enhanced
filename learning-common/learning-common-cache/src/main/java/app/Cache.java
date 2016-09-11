package app;

import java.util.Map;
import java.util.Set;

/**
 * Created by lili19289 on 2016/8/29.
 */
public interface Cache {

    public void set(String key, String value);

    public void set(String key, String value, int expireSecs);

    public String get(String key);

    public boolean expire(String key, int expireSecs);

    public boolean persist(String key);

    public boolean delete(String key);

    public boolean exists(String key);

    public long incrBy(String key, long by);

    public long incr(String key);

    public long decrBy(String key, long by);

    public long decr(String key);

    public Long zadd(String key , double expire , String member);

    public Long zcard(String key);

    public Set<String> zrange(String key, long start, long end);

    public Long ttl(String key);

    Long setnx(String key);

    Long setnx(String key, String value, int expireTime);
}
