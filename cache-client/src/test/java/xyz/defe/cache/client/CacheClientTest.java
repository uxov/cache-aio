package xyz.defe.cache.client;

import org.junit.jupiter.api.Test;
import xyz.defe.cache.common.Message;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CacheClientTest {
    final String ip = "127.0.0.1";
    final int port = 9120;

    @Test
    public void testCacheClient() {
        CacheClient cacheClient = new CacheClient(ip, port);

        Message testObject = new Message();
        String key = "testObject";
        testObject.setValue(TestData.message);

        int count = cacheClient.put(key, testObject);
        assertEquals(1, count);
        Message obj = (Message) cacheClient.get(key);
        assertEquals(obj.getValue(), testObject.getValue());
        Set<String> set = cacheClient.getKeys();
        count = set.size();
        assertTrue(set.contains(key));
        boolean boo = cacheClient.delete(key);
        assertTrue(boo);
        set = cacheClient.getKeys();
        assertTrue(set.size() == count - 1);
    }

}

