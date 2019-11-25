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


@ChannelHandler.Sharable
public abstract class PeerNetwork extends ChannelInboundHandlerAdapter {

    private ChannelGroup channelGroup;

    LycoinApplicationContext context;
    public PeerNetwork(LycoinApplicationContext context){
        this.context = context;
        this.channelGroup= new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    }


    public abstract void onReceiveMessage(ChannelHandlerContext ctx,Message message);


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
