package com.ying.cloud.lycoin.net.netty;

import com.ying.cloud.lycoin.net.IConnectAdapter;
import com.ying.cloud.lycoin.net.Message;
import com.ying.cloud.lycoin.net.PeerNode;
import com.ying.cloud.lycoin.net.Source;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class NettyClientNode<TSource extends ChannelSource> extends PeerNode<TSource> {
    private ChannelGroup channelGroup;
    private Bootstrap bootstrap;

    public void setConnectAdapter(IConnectAdapter<TSource> connectAdapter) {
        this.connectAdapter = connectAdapter;
    }

    private IConnectAdapter<TSource> connectAdapter;

    public NettyClientNode(){
        this.channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    }
    @Override
    public void setup() {
        bootstrap = new Bootstrap();
        NioEventLoopGroup group = new NioEventLoopGroup();
        bootstrap.group(group).channel(NioSocketChannel.class).handler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) {
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
                        int port = nioSocketChannel.remoteAddress().getPort();
                        TSource source =  getSource(host+":"+port);
                        source.setSender(ctx.channel());
                        channelGroup.add(ctx.channel());
                        connectAdapter.onActive(source);

                    }

                    @Override
                    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                        NioSocketChannel nioSocketChannel =(NioSocketChannel)ctx.channel();
                        String host=nioSocketChannel.remoteAddress().getAddress().getHostAddress();
                        int port = nioSocketChannel.remoteAddress().getPort();
                        TSource source = getSource(host+":"+port);
                        source.setSender(null);
                        channelGroup.remove(ctx.channel());
                        connectAdapter.onInactive(source);

                        System.out.println("连接中断,2s后重连。");

                        ctx.channel().eventLoop().schedule(new Runnable() {
                            @Override
                            public void run() {
                               connect(host,port);
                            }
                        }, 2, TimeUnit.SECONDS);
                    }

                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        try{

                            NioSocketChannel nioSocketChannel =(NioSocketChannel)ctx.channel();
                            String host=nioSocketChannel.remoteAddress().getAddress().getHostAddress();
                            int port = nioSocketChannel.remoteAddress().getPort();
                            TSource source = getSource(host+":"+port);
                            handler.handle(source,(Message)msg);
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


        for (int i = 0; i <sources.size() ; i++) {
            connect(sources.get(i).host,sources.get(i).port);
        }
    }

    @Override
    public void send(TSource source, Message message) {
        if(source!=null){
            Channel channel = source.getSender();
            if(channel!=null && channel.isActive()){
                channel.writeAndFlush(message);
            }
        }
    }


    private void connect(String host,int port){
        ConnectionListener listener = new ConnectionListener(bootstrap,host,port);
        bootstrap.connect(host,port).addListener(listener);
    }

    public void connectSource(TSource source) {
        ChannelSource find = getSource(source.id());
        if(find==null){
            addSource(source);
            connect(source.host,source.port);
        }

    }
}
