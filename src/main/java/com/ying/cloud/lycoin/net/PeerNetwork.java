package com.ying.cloud.lycoin.net;

import com.ying.cloud.lycoin.message.Message;
import com.ying.cloud.lycoin.message.MessageHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

@ChannelHandler.Sharable
public class PeerNetwork extends ChannelInboundHandlerAdapter implements IPeerNetwork {

    private ChannelGroup channelGroup;

    private List<MessageHandler> handlers;

    public PeerNetwork(){
        this.channelGroup= new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
        this.handlers = new ArrayList<>();

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println(msg.toString());
        Message message = (Message)msg;
        trigger(message);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        channelGroup.add(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.err.println(cause.getMessage());
    }

    @Override
    public void broadcast(Message message) {
        channelGroup.writeAndFlush(message);
    }

    @Override
    public void handler(MessageHandler handler) {
        this.handlers.add(handler);
    }

    @Override
    public void trigger(Message message){
        for (int i = 0; i < handlers.size(); i++) {
            MessageHandler handler = this.handlers.get(i);
            ParameterizedType type = (ParameterizedType)(handler.getClass().getGenericInterfaces()[0]);
            if(message.getClass().equals(type.getActualTypeArguments()[0])){
                this.handlers.get(i).handle(this,message);
            }

        }
    }


}
