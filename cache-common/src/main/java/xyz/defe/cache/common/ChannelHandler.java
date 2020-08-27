package xyz.defe.cache.common;

import java.nio.channels.AsynchronousSocketChannel;

public interface ChannelHandler {
    void read(AsynchronousSocketChannel channel, CompleteMessageHandler completeMessageHandler);

    <T> void write(AsynchronousSocketChannel channel, T object, ActionAfterWriteMessage actionAfterWriteMessage);
}
