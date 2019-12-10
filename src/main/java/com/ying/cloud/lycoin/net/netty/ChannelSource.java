package com.ying.cloud.lycoin.net.netty;

import com.ying.cloud.lycoin.net.Source;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

public class ChannelSource extends Source {

    public ChannelSource(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String host;
    public int port;

    public Channel getReceiver() {
        return receiver;
    }

    public void setReceiver(Channel receiver) {
        this.receiver = receiver;
    }

    public Channel getSender() {
        return sender;
    }

    public void setSender(Channel sender) {
        this.sender = sender;
    }

    private Channel receiver;
    private Channel sender;


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
