package xyz.defe.cache.server;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class CacheManager {
    private final ConcurrentHashMap<String, Object> cacheMap = new ConcurrentHashMap();
    private Logger log = Logger.getLogger(this.getClass().getName());

    public int put(String key, Object data) {
        cacheMap.put(key, data);
        log.info("save object(key=" + key + ") successful");
        return 1;
    }

    public Object get(String key) {
        log.info("get '" + key + "' from cache");
        return cacheMap.get(key);
    }

    public boolean delete(String key) {
        cacheMap.remove(key);
        log.info("delete '" + key + "' object successful");
        return true;
    }

    public Set<String> list() {
        log.info("get key set from cache");
        return new HashSet<>(cacheMap.keySet());
    }
}
