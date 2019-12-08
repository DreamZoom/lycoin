package com.ying.cloud.lycoin.net.netty;

import com.ying.cloud.lycoin.net.Source;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

public class ChannelSource extends Source {

    public ChannelSource(String host, int port, Channel channel) {
        this.host = host;
        this.port = port;
        this.channel = channel;
    }

    protected String host;
    protected int port;

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    private Channel channel;

    public ChannelSource(Channel channel){
        this.channel = channel;
    }


    @Override
    public boolean equals(Object obj) {
        if(obj==null)return  false;
        return this.id().equals(((Source)obj).id());
    }

    @Override
    public String toString() {
        return id();
    }

    @Override
    public String id() {
        return host+":"+port;
    }
}
