package com.ying.cloud.lycoin.net.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoop;

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
            ConnectionListener listener =this;
            loop.schedule(new Runnable() {
                @Override
                public void run() {
                    System.out.println("服务端链接失败，开始重连操作...");
                    System.out.println("ip:"+ip+"---"+"port:"+port);
                    //NioSocketChannel address = ((NioSocketChannel)future.channel());
                    bootstrap.connect(ip,port).addListener(listener);
                }
            }, 1L, TimeUnit.SECONDS);
        } else {
            System.out.println("服务端链接成功...");
            System.out.println("ip:"+ip+"---"+"port:"+port);
        }
    }
}
