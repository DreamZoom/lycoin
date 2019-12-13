package com.ying.cloud.lycoin.net.netty;

import com.ying.cloud.lycoin.net.ISourceAdapter;
import com.ying.cloud.lycoin.net.Message;
import com.ying.cloud.lycoin.net.PeerNode;
import com.ying.cloud.lycoin.net.Source;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class NettyServerNode<TSource extends ChannelSource> extends PeerNode<TSource> {

    private ChannelGroup channelGroup;
    public NettyServerNode(String host,int port) {
        this.host = host;
        this.port = port;
        this.channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    }

    public ISourceAdapter getSourceAdapter() {
        return sourceAdapter;
    }

    public void setSourceAdapter(ISourceAdapter sourceAdapter) {
        this.sourceAdapter = sourceAdapter;
    }

    protected ISourceAdapter sourceAdapter;

    private int port;
    private String host;
    @Override
    public void setup() {
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
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelActive(ChannelHandlerContext ctx) throws Exception {

                                NioSocketChannel nioSocketChannel =(NioSocketChannel)ctx.channel();
                                String host=nioSocketChannel.remoteAddress().getAddress().getHostAddress();
                                int port = nioSocketChannel.localAddress().getPort();

                                ChannelSource source =  getSource(host+":"+port);
                                if(source!=null){
                                    source.setReceiver(ctx.channel());
                                }
                                else {
                                    source =  new ChannelSource(host,port);
                                    source.setReceiver(ctx.channel());
                                    sourceAdapter.onAdded(source);
                                }
                                channelGroup.add(ctx.channel());
                            }

                            @Override
                            public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                                NioSocketChannel nioSocketChannel =(NioSocketChannel)ctx.channel();
                                String host=nioSocketChannel.remoteAddress().getAddress().getHostAddress();
                                int port = nioSocketChannel.localAddress().getPort();

                                ChannelSource source = getSource(host+":"+port);
                                if(source!=null){
                                    source.setSender(null);
                                }
                                else{
                                    source =  new ChannelSource(host,port);
                                    sourceAdapter.onRemoved(source);
                                }

                                channelGroup.remove(ctx.channel());
                                ctx.close();
                            }

                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                try{
                                    ctx.channel().eventLoop().schedule(new Runnable() {
                                        @Override
                                        public void run() {
                                            NioSocketChannel nioSocketChannel =(NioSocketChannel)ctx.channel();
                                            String host=nioSocketChannel.remoteAddress().getAddress().getHostAddress();
                                            int port = nioSocketChannel.localAddress().getPort();
                                            TSource source = getSource(host+":"+port);
                                            handler.handle(source,(Message)msg);
                                        }
                                    },0,TimeUnit.SECONDS);

                                }
                                catch (Exception error){
                                    System.out.println(error.getMessage());
                                }
                            }

                            @Override
                            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                //super.exceptionCaught(ctx, cause);
                                System.out.println(cause.getMessage());
                            }
                        });
                    }
                });


        serverBootstrap.bind(port);
    }

    @Override
    public void broadcast(Message message) {
        channelGroup.writeAndFlush(message);
    }


    @Override
    public void send(TSource source, Message message) {
        if(source!=null){
            Channel channel = source.getReceiver();
            if(channel!=null && channel.isActive()){
                channel.writeAndFlush(message);
            }
        }
    }
}
