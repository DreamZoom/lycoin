package com.ying.cloud.lycoin.net.netty;

import com.ying.cloud.lycoin.models.Message;

import com.ying.cloud.lycoin.net.Source;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

@ChannelHandler.Sharable
public class NettyChannelInboundHandler extends ChannelInboundHandlerAdapter {

    public NettyChannelInboundHandler(NettyNetwork nettyNetwork) {
        this.nettyNetwork = nettyNetwork;
    }

    NettyNetwork nettyNetwork;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ChannelSource source =new ChannelSource(ctx);
        nettyNetwork.addSource(source);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ChannelSource source =new ChannelSource(ctx);
        nettyNetwork.removeSource(source);
        super.channelInactive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Source source =new ChannelSource(ctx);
        Message message =(Message)msg;
        receiveMessage(source,message);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.err.println(cause.getMessage());
    }

    public void receiveMessage(Source source , Message message){
        nettyNetwork.receiveMessage(source,message);
    }
}
