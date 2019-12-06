package com.ying.cloud.lycoin.net.netty;

import com.ying.cloud.lycoin.LycoinContext;
import com.ying.cloud.lycoin.config.Peer;
import com.ying.cloud.lycoin.models.Message;
import com.ying.cloud.lycoin.net.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

import java.util.List;

public class NettyNetwork extends Network {

    public NettyNetwork(LycoinContext context) {
        this.context = context;
    }

    private LycoinContext context;


    @Override
    public void setup() {


        ChannelInboundHandlerAdapter channelInboundHandlerAdapter =new NettyChannelInboundHandler(this);

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
                        ch.pipeline().addLast(channelInboundHandlerAdapter);
                    }
                });


        int port = context.getConfig().getServerPort();
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
                        ch.pipeline().addLast(channelInboundHandlerAdapter);
                    }
                });



        List<Peer> peers = context.getConfig().getPeers();
        for (int i = 0; i < peers.size(); i++) {
            Peer peer = peers.get(i);
            ConnectionListener listener = new ConnectionListener(bootstrap,peer.getIp(),peer.getServerPort());
            bootstrap.connect(peer.getIp(), peer.getServerPort()).addListener(listener);
        }
    }



    @Override
    public void sendMessage(Source source, Message message) {

        ChannelSource channelSource = (ChannelSource)source;
        channelSource.getChannel().writeAndFlush(message);
    }

    @Override
    public void removeSource(Source source) {
        super.removeSource(source);
    }

}
