package com.ying.cloud.lycoin.net.netty;

import com.ying.cloud.lycoin.net.Message;
import com.ying.cloud.lycoin.net.PeerNode;
import com.ying.cloud.lycoin.net.Source;
import io.netty.bootstrap.Bootstrap;
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

public class NettyClientNode extends PeerNode<ChannelSource> {
    private ChannelGroup channelGroup;
    private Bootstrap bootstrap;

    public NettyClientNode(){
        this.channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    }
    @Override
    public void setup() {
        bootstrap = new Bootstrap();
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
                ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                    @Override
                    public void channelActive(ChannelHandlerContext ctx) throws Exception {
                        NioSocketChannel nioSocketChannel =(NioSocketChannel)ctx.channel();
                        String host=nioSocketChannel.remoteAddress().getHostName();
                        int port = nioSocketChannel.remoteAddress().getPort();
                        ChannelSource source =new ChannelSource(host,port,ctx.channel());
                        sources.add(source);System.out.println(source);

                        channelGroup.add(ctx.channel());
                    }

                    @Override
                    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                        sources.removeIf((source)->{
                            return source.getChannel().equals(ctx.channel());
                        });
                        channelGroup.remove(ctx.channel());
                    }

                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        try{

                            handler.handle(ctx.channel(),(Message)msg);
                        }
                        catch (Exception error){
                            System.out.println(error.getMessage());
                        }
                    }
                });
            }
        });
    }

    @Override
    public void send(ChannelSource source, Message message) {
        ChannelSource source1 =sources.findById(source);
        if(source1!=null){
            source1.getChannel().writeAndFlush(message);
        }
    }


    public void connect(String host,int port){
        bootstrap.connect(host,port);
    }
}