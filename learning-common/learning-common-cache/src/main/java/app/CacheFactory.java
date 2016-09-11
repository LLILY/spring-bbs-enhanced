package app;

import app.memcached.MemCachedImpl;
import app.redis.RedisCacheImpl;

/**
 * Created by lili19289 on 2016/8/29.
 */
public abstract class CacheFactory {
    public static Cache cache;

    public static final String PROP_KEY_PREFIX   = "cache.";

    public static final String PROP_KEY_IMPLTYPE = "implType";

    public static Cache createCache() {
        String implType = "redis";
        if (CacheImplTypes.MEMCACHED.equals(implType)) {
            return MemCachedImpl.getInstance();
        } else if (CacheImplTypes.REDIS.equals(implType)) {
            return RedisCacheImpl.getInstance();
        } else {
            throw new IllegalArgumentException("invalid cacheImpl: " + implType);
        }
    }
}
