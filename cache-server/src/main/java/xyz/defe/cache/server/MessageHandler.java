package xyz.defe.cache.server;

import xyz.defe.cache.common.*;

import java.nio.channels.AsynchronousSocketChannel;

public class MessageHandler {
    private CacheManager cacheManager;
    private final ChannelHandler channelHandler = new ChannelHandlerImpl();

    public MessageHandler(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    void recieveAndSend(AsynchronousSocketChannel channel){
        channelHandler.read(channel, new CompleteMessageHandler() {  //read incoming data
            //when get complete message data
            @Override
            public void process(Object data, AsynchronousSocketChannel asc) throws Exception {
                //keep listen on this channel for next request data
                channelHandler.read(asc, this);
                Message message = processMsg((Message) data);
                channelHandler.write(asc, message, null);
            }

            @Override
            public void failed(Throwable exc) { exc.printStackTrace(); }
        });
    }

    private Message processMsg(Message m) {
        switch (m.getOperation()) {
            case Constants.OPERATION_PUT -> m.setValue(cacheManager.put(m.getKey(), m.getValue()));
            case Constants.OPERATION_GET -> m.setValue(cacheManager.get(m.getKey()));
            case Constants.OPERATION_DELETE -> m.setValue(cacheManager.delete(m.getKey()));
            case Constants.OPERATION_GET_KEYS -> m.setValue(cacheManager.list());
        }
        return m;
    }
}
