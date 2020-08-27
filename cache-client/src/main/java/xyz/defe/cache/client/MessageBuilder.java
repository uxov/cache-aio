package xyz.defe.cache.client;

import xyz.defe.cache.common.Constants;
import xyz.defe.cache.common.Message;

public class MessageBuilder {
    public static <T> Message put(String key, T data) {
        Message m = new Message();
        m.setKey(key);
        m.setValue(data);
        m.setOperation(Constants.OPERATION_PUT);
        return m;
    }

    public static Message get(String key) {
        Message m = new Message();
        m.setKey(key);
        m.setOperation(Constants.OPERATION_GET);
        return m;
    }

    public static Message delete(String key) {
        Message m = new Message();
        m.setKey(key);
        m.setOperation(Constants.OPERATION_DELETE);
        return m;
    }

    public static Message getKeys() {
        Message m = new Message();
        m.setOperation(Constants.OPERATION_GET_KEYS);
        return m;
    }
}
