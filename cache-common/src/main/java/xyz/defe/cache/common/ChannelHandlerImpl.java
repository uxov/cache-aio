package xyz.defe.cache.common;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.logging.Logger;

public class ChannelHandlerImpl implements ChannelHandler {
    static final int INT_BYTES = Integer.BYTES;
    private final Logger log = Logger.getLogger(this.getClass().getName());

    public void read(AsynchronousSocketChannel channel, CompleteMessageHandler completeMessageHandler) {
        if (completeMessageHandler == null) {
            throw new NullPointerException("CompleteMessageHandler can't be null!");
        }
        ByteBuffer lenBuf = ByteBuffer.allocate(INT_BYTES);
        channel.read(lenBuf, null, new CompletionHandler<Integer, Object>() {
            @Override
            public void completed(Integer result, Object attachment) {
                if (result > 0) {
                    if (lenBuf.position() < INT_BYTES - 1) {
                        //not enough bytes to read the data length field then keep read
                        channel.read(lenBuf, null, this);
                        return;
                    }
                    lenBuf.flip();
                    int dataLength = lenBuf.getInt();
                    ByteBuffer messageBuf = ByteBuffer.allocate(dataLength);
                    channel.read(messageBuf, null, new CompletionHandler<Integer, Object>() {
                        @Override
                        public void completed(Integer result, Object attachment) {
                            if (messageBuf.position() == dataLength) {  //when get complete message data
                                try {
                                    messageBuf.flip();
                                    completeMessageHandler.process(KryoUtil.deserialize(messageBuf.array()), channel);
                                } catch (Exception e) {
                                    completeMessageHandler.failed(e);
                                }
                            } else {
                                channel.read(messageBuf, null, this);
                            }
                        }

                        @Override
                        public void failed(Throwable exc, Object attachment) {
                            exc.printStackTrace();
                        }
                    });
                } else {
                    try {
                        channel.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                try {
                    channel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public <T> void write(AsynchronousSocketChannel channel, T object, ActionAfterWriteMessage actionAfterWriteMessage) {
        byte[] bytes = KryoUtil.serialize(object);
        ByteBuffer buffer = ByteBuffer.allocate(INT_BYTES + bytes.length);
        buffer.putInt(bytes.length);
        buffer.put(bytes);
        buffer.flip();
        channel.write(buffer, null, new CompletionHandler<Integer, AsynchronousSocketChannel>() {
            @Override
            public void completed(Integer result, AsynchronousSocketChannel attachment) {
                if (result > -1) {
                    if (buffer.hasRemaining()) {
                        channel.write(buffer, null, this);
                    } else {    //message data has been written
                        if (actionAfterWriteMessage != null) {
                            try {
                                actionAfterWriteMessage.doIt(channel);
                            } catch (Exception e) {
                                actionAfterWriteMessage.failed(e);
                            }
                        }
                    }
                } else {
                    try {
                        channel.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void failed(Throwable exc, AsynchronousSocketChannel attachment) {
                exc.printStackTrace();
            }
        });
    }

}
