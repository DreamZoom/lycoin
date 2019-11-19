package com.ying.cloud.lycoin.net;

import com.google.gson.Gson;
import com.ying.cloud.lycoin.domain.Peer;
import com.ying.cloud.lycoin.domain.PeerConfig;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PeerNetwork {

    List<Channel> channels;
    PeerConfig config;
    public PeerNetwork(PeerConfig config){
        channels = new ArrayList<>();
        Collections.synchronizedList(channels);
        this.config = config;
    }

    public void setupServer(){
        ServerBootstrap serverBootstrap = new ServerBootstrap();

        NioEventLoopGroup boos = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        serverBootstrap
                .group(boos, worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    protected void initChannel(NioSocketChannel ch) {
                        ch.pipeline().addLast(new StringDecoder());
                        ch.pipeline().addLast(new SimpleChannelInboundHandler<String>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, String msg) {
                                System.out.println(msg);
                                ctx.flush();
                            }
                        });

                        channels.add(ch);
                    }
                })
                .bind(config.getPort());

        System.out.println("server setup 8000");
    }

    public void connectNodes(){
        List<Peer> peers = config.getPeers();
        for (int i = 0; i < peers.size(); i++) {
            Peer peer = peers.get(i);
            Bootstrap bootstrap = new Bootstrap();
            NioEventLoopGroup group = new NioEventLoopGroup();

            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) {
                            ch.pipeline().addLast(new StringEncoder());
                        }
                        
                    });

            Channel channel = bootstrap.connect(peer.getIp(), peer.getPort()).channel();
            channels.add(channel);
            System.out.println("connect to "+channel.id());
        }
    }

    public void broadcast(){
        for (int i = 0; i <channels.size(); i++) {
            channels.get(i).writeAndFlush("hello");
        }
    }

}
