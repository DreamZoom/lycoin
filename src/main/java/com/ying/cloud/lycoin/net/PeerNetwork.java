package com.ying.cloud.lycoin.net;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ying.cloud.lycoin.LycoinApplicationContext;
import com.ying.cloud.lycoin.models.Block;
import com.ying.cloud.lycoin.models.BlockChain;
import com.ying.cloud.lycoin.models.Message;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


@ChannelHandler.Sharable
public  class PeerNetwork extends ChannelInboundHandlerAdapter {

    private ChannelGroup channelGroup;

    private List<LycoinMessageHandler> handlers;

    LycoinApplicationContext context;
    public PeerNetwork(LycoinApplicationContext context){
        this.context = context;
        this.channelGroup= new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
        this.handlers = new ArrayList<>();
    }


    public void hander(LycoinMessageHandler messageHandler){
        this.handlers.add(messageHandler);
    }


    public void onReceiveMessage(ChannelHandlerContext ctx,Message message){
        for (int i = 0; i < handlers.size(); i++) {
            LycoinMessageHandler handler = this.handlers.get(i);
            ParameterizedType type = (ParameterizedType)(handler.getClass().getGenericInterfaces()[0]);
            if(message.getClass().equals(type.getActualTypeArguments()[0])){
                this.handlers.get(i).handle(ctx,this,message);
            }

        }
    }


    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        channelGroup.add(ctx.channel());
        context.fireEvent("connected",this,ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        onReceiveMessage(ctx, (Message)msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.err.println(cause.getMessage());
    }

    public void send_message(ChannelHandlerContext ctx,Message message){
        ctx.writeAndFlush(message);
    }


    public void broadcast(Message message){
        channelGroup.writeAndFlush(message);
    }
}
