package com.ying.cloud.lycoin.net.netty;

import com.ying.cloud.lycoin.config.BlockConfig;
import com.ying.cloud.lycoin.config.Peer;
import com.ying.cloud.lycoin.net.Message;
import com.ying.cloud.lycoin.net.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.List;

public class NettyNetwork extends PeerNode {
    private BlockConfig config;
    private ChannelGroup channelGroup ;


    public NettyNetwork(BlockConfig config) {
        this.config = config;
        this.channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    }

    @Override
    public void setup() {

        ChannelInboundHandlerAdapter serverAdapter =new ChannelInboundHandlerAdapter(){
            @Override
            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                channelGroup.add(ctx.channel());
            }

            @Override
            public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                channelGroup.remove(ctx.channel());
            }

            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                handler.handle(ctx.channel(),(Message)msg);
            }
        };
        ChannelInboundHandlerAdapter clientAdapter =new ChannelInboundHandlerAdapter(){
            @Override
            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                channelGroup.add(ctx.channel());
            }

            @Override
            public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                channelGroup.remove(ctx.channel());
            }

            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                handler.handle(ctx.channel(),(Message)msg);
            }
        };

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        NioEventLoopGroup boos = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        serverBootstrap
                .group(boos, worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) {
                        System.out.println("accept a socket");

                        ch.config().setAllowHalfClosure(true);
                        ch.pipeline().addLast(new LengthFieldPrepender(4));
                        ch.pipeline().addLast(new MessageEncoder());

                        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024*500,0,4,0,4));
                        ch.pipeline().addLast(new MessageDecoder());
                        ch.pipeline().addLast(serverAdapter);
                    }
                });


        int port = config.getServerPort();
        serverBootstrap.bind(port);


        Bootstrap bootstrap = new Bootstrap();
        NioEventLoopGroup group = new NioEventLoopGroup();
        bootstrap.group(group)
                .channel(NioSocketChannel.class).handler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) {
                        System.out.println("accept a socket");
                        ch.config().setAllowHalfClosure(true);

                        ch.pipeline().addLast(new LengthFieldPrepender(4));
                        ch.pipeline().addLast(new MessageEncoder());

                        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024*500,0,4,0,4));
                        ch.pipeline().addLast(new MessageDecoder());
                        ch.pipeline().addLast(clientAdapter);
                    }
                });

        List<Peer> peers = config.getPeers();
        for (int i = 0; i < peers.size(); i++) {
            Peer peer = peers.get(i);
            ConnectionListener listener = new ConnectionListener(bootstrap,peer.getIp(),peer.getServerPort());
            bootstrap.connect(peer.getIp(), peer.getServerPort()).addListener(listener);
        }
    }

    @Override
    public void broadcast(Message message) {
        super.broadcast(message);
    }

    @Override
    public void send(Source source, Message message) {
        ChannelSource channelSource = (ChannelSource)source;
        if(channelSource!=null){
            channelSource.getChannel().writeAndFlush(message);
        }
    }



}
