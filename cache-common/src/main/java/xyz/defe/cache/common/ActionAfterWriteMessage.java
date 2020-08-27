package xyz.defe.cache.common;

import java.nio.channels.AsynchronousSocketChannel;

public interface ActionAfterWriteMessage {
    void doIt(AsynchronousSocketChannel channel) throws Exception;

    void failed(Throwable exc);
}
