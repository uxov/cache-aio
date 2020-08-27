package xyz.defe.cache.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

public class ConnectionPool {
    private String ip;
    private int port;
    private int initPooSize;
    private int maxPoolSize;
    private int poolSize;
    private int increaseCount;
    private final ConcurrentLinkedQueue<AsynchronousSocketChannel> channels = new ConcurrentLinkedQueue<>();
    private Logger log = Logger.getLogger(this.getClass().getName());

    public ConnectionPool(ClientOptions options) {
        this.ip = options.getIp();
        this.port = options.getPort();
        maxPoolSize = options.getMaxPoolSize();
        increaseCount = options.getIncreaseCount();
        poolSize = initPooSize = options.getInitPooSize();
        init();
    }

    private void init() {
        for (int i = 0; i < initPooSize; i++) {
            try {
                AsynchronousSocketChannel channel = AsynchronousSocketChannel.open();
                channel.connect(new InetSocketAddress(ip, port)).get();
                channels.add(channel);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (channels.isEmpty()) {
            log.info("Failed to connect cache server!");
            System.exit(0);
        }
    }

    AsynchronousSocketChannel newConnection() throws ExecutionException, InterruptedException, IOException {
        AsynchronousSocketChannel channel = AsynchronousSocketChannel.open();
        synchronized (this) {
            channel.connect(new InetSocketAddress(ip, port)).get();
        }
        return channel;
    }

    private void createConnections(int count) throws ExecutionException, InterruptedException, IOException {
        for (int i = count; i > 0; i--) {
            AsynchronousSocketChannel channel = AsynchronousSocketChannel.open();
            channel.connect(new InetSocketAddress(ip, port)).get();
            channels.add(channel);
        }
        poolSize += count;
    }

    AsynchronousSocketChannel getChannelFromQueue() throws InterruptedException, ExecutionException, IOException {
        AsynchronousSocketChannel channel = channels.poll();
        if (channel != null) {return channel;}
        synchronized (this) {
            if (poolSize + increaseCount <= maxPoolSize) {
                createConnections(increaseCount);
            } else if (maxPoolSize - poolSize < increaseCount) {
                createConnections(maxPoolSize - poolSize);
            } else {
                log.info("Over max pool size, can't create new connection!");
            }
        }
        channel = channels.poll();
        return channel;
    }

    void takeBack(AsynchronousSocketChannel channel) {
        if (channel != null) {
            channels.add(channel);
        }
    }

    void reducePoolSize() throws IOException {
        int count = poolSize - initPooSize;
        AsynchronousSocketChannel channel;
        while (count > 0) {
            channel = channels.poll();
            channel.close();
            count--;
        }
    }

    public int getInitPooSize() {
        return initPooSize;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public int getPoolSize() {
        return poolSize;
    }
}
