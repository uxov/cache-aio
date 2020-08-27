package xyz.defe.cache.client;

import xyz.defe.cache.common.*;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.HashSet;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

public class MessageHandler {
    private ClientOptions options;
    private ConnectionPool connectionPool;
    private final ChannelHandler channelHandler = new ChannelHandlerImpl();
    final ConcurrentHashMap<String, Message> messageMap = new ConcurrentHashMap();
    private Logger log = Logger.getLogger(this.getClass().getName());

    public MessageHandler(ConnectionPool connectionPool, ClientOptions options) {
        this.options = options;
        this.connectionPool = connectionPool;
    }

    Object process(Message message) {
        Object result = null;
        try {
            sendAndRecieve(message);
            Message msg = getResult(message.getId());
            if (msg == null) {
                switch (message.getOperation()) {
                    case Constants.OPERATION_PUT -> result = 0;
                    case Constants.OPERATION_DELETE -> result = false;
                    case Constants.OPERATION_GET_KEYS -> result = new HashSet<String>();
                }
            } else {
                result = msg.getValue();
            }
            messageMap.remove(message.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private void sendAndRecieve(Message message) throws InterruptedException, ExecutionException, IOException {
        boolean takeBack = true;
        AsynchronousSocketChannel asc = connectionPool.getChannelFromQueue();
        if (asc == null) {
            asc = connectionPool.newConnection();
            takeBack = false;
        }
        Boolean finalTakeBack = takeBack;
        channelHandler.write(asc, message, new ActionAfterWriteMessage() {
            @Override
            public void doIt(AsynchronousSocketChannel channel) {
                //prepare to read response data after request data has been written
                channelHandler.read(channel, new CompleteMessageHandler() {
                    @Override
                    public void process(Object object, AsynchronousSocketChannel channel) throws Exception {
                        Message msg = (Message) object;
                        messageMap.put(msg.getId(), msg);
                        if (finalTakeBack) {
                            connectionPool.takeBack(channel);
                        } else {
                            channel.close();
                        }
                    }

                    @Override
                    public void failed(Throwable exc) {
                        exc.printStackTrace();
                    }
                });
            }

            @Override
            public void failed(Throwable exc) {
                exc.printStackTrace();
            }
        });
    }

    Message getResult(String messageId) {
        Message message = null;
        AtomicBoolean wait = new AtomicBoolean(true);
        CompletableFuture<Message> future = CompletableFuture.supplyAsync(() -> {
            while (messageMap.get(messageId) == null && wait.get()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Message msg = messageMap.get(messageId);
            return msg;
        });
        try {
            message = future.get(options.getRequestTimeOut(), TimeUnit.SECONDS);
        } catch (TimeoutException te) {
            wait.set(false);
            log.info("request time out");
        } catch (Exception e) {
            wait.set(false);
            e.printStackTrace();
        }
        return message;
    }

}
