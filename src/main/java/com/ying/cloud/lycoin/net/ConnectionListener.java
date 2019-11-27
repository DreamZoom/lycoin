package com.ying.cloud.lycoin.net;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoop;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;

public class ConnectionListener implements ChannelFutureListener {

    Bootstrap bootstrap ;
    String ip;
    int port;
    public ConnectionListener(Bootstrap bootstrap,String ip,int port){
        this.bootstrap = bootstrap;
        this.ip = ip;
        this.port = port;
    }

    @Override
    public void operationComplete(ChannelFuture future) throws Exception {
        if (!future.isSuccess()) {
            final EventLoop loop = future.channel().eventLoop();
            loop.schedule(new Runnable() {
                @Override
                public void run() {
                    System.err.println("服务端链接失败，开始重连操作...");
                    //NioSocketChannel address = ((NioSocketChannel)future.channel());
                    bootstrap.connect(ip,port);
                }
            }, 1L, TimeUnit.SECONDS);
        } else {
            System.err.println("服务端链接成功...");
            System.err.println("ip:"+ip+"---"+"port:"+port);
        }
    }
}
