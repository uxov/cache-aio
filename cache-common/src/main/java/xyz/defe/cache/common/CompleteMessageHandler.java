package xyz.defe.cache.common;

import java.nio.channels.AsynchronousSocketChannel;

public interface CompleteMessageHandler {
    void process(Object data, AsynchronousSocketChannel channel) throws Exception;

    void failed(Throwable exc);
}
