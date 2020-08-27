package xyz.defe.cache.server;

import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class Server {
    private int port;
    private MessageHandler messageHandler;
    private final Object obj = new Object();
    private final int poolSize = Runtime.getRuntime().availableProcessors() * 10;
    private final Logger log = Logger.getLogger(this.getClass().getName());

    public Server(int port) {
        this.port = port;
        messageHandler = new MessageHandler(new CacheManager());
    }

    public void start() {
        try {
            AsynchronousChannelGroup group = AsynchronousChannelGroup
                    .withFixedThreadPool(poolSize, Executors.defaultThreadFactory());
            AsynchronousServerSocketChannel assc = AsynchronousServerSocketChannel.open(group);
            assc.bind(new InetSocketAddress(port));

            assc.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {
                @Override
                public void completed(AsynchronousSocketChannel channel, Object attachment) {
                    assc.accept(null, this);    //prepare to accept next request
                    messageHandler.recieveAndSend(channel);
                }

                @Override
                public void failed(Throwable exc, Object attachment) {
                    exc.printStackTrace();
                }
            });
            log.info("start server");
            synchronized (obj) { obj.wait(); }  //keep main thread not exit
            group.shutdownNow();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        synchronized (obj) { obj.notify(); }
        log.info("server is stopped");
    }
}