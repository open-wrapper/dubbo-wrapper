package com.wrapper.dubbo.exchanger.version;

import org.apache.dubbo.remoting.Channel;
import org.apache.dubbo.remoting.ChannelHandler;
import org.apache.dubbo.remoting.RemotingException;
import org.apache.dubbo.remoting.exchange.Request;
import org.apache.dubbo.remoting.transport.ChannelHandlerDelegate;

/**
 * @author codel
 * @since 2020-01-02
 */
public class VersionHeaderExchangeHandler implements ChannelHandlerDelegate {

    private static final String COMPATIBLE_VERSION = "2.5.3";

    private ChannelHandler handler;

    public VersionHeaderExchangeHandler(ChannelHandler handler) {
        this.handler = handler;
    }

    @Override
    public ChannelHandler getHandler() {
        if (handler instanceof ChannelHandlerDelegate) {
            return ((ChannelHandlerDelegate) handler).getHandler();
        }
        return handler;
    }

    @Override
    public void connected(Channel channel) throws RemotingException {
        handler.connected(channel);
    }

    @Override
    public void disconnected(Channel channel) throws RemotingException {
        handler.disconnected(channel);

    }

    @Override
    public void sent(Channel channel, Object message) throws RemotingException {
        handler.sent(channel, message);

    }

    @Override
    public void received(Channel channel, Object message) throws RemotingException {
        if (message instanceof Request) {
            Request request = (Request) message;
            request.setVersion(COMPATIBLE_VERSION);
        }
        handler.received(channel, message);

    }

    @Override
    public void caught(Channel channel, Throwable exception) throws RemotingException {
        handler.received(channel, exception);

    }
}
