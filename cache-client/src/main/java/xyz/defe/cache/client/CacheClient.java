package xyz.defe.cache.client;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class CacheClient {
    private ClientOptions options;
    private ConnectionPool connectionPool;
    private MessageHandler messageHandler;
    private final Logger log = Logger.getLogger(this.getClass().getName());

    public CacheClient(String ip, int port) {
        options = new ClientOptions();
        options.setIp(ip).setPort(port);
        init(options);
    }

    public CacheClient(ClientOptions options) {
        this.options = options;
        init(options);
    }

    private void init(ClientOptions options) {
        connectionPool = new ConnectionPool(options);
        messageHandler = new MessageHandler(connectionPool, options);
        CompletableFuture.runAsync(() -> checkMessageMapSize());
    }

    public <T> int put(String key, T data) {
        return (int) messageHandler.process(MessageBuilder.put(key, data));
    }

    public Object get(String key) {
        return messageHandler.process(MessageBuilder.get(key));
    }

    public boolean delete(String key) {
        return (boolean) messageHandler.process(MessageBuilder.delete(key));
    }

    public Set<String> getKeys() {
        return (Set<String>) messageHandler.process(MessageBuilder.getKeys());
    }

    private void checkMessageMapSize() {
        while (true) {
            int mapSize = messageHandler.messageMap.size();
            if (mapSize < connectionPool.getInitPooSize() * 5
                    && connectionPool.getPoolSize() > connectionPool.getInitPooSize()) {
                try {
                    connectionPool.reducePoolSize();
                    Thread.sleep(options.poolCheckPeriod);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public ClientOptions getOptions() {
        return options;
    }
}
