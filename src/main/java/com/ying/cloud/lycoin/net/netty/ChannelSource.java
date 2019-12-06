package com.ying.cloud.lycoin.net.netty;

import com.ying.cloud.lycoin.net.Source;
import io.netty.channel.ChannelHandlerContext;

public class ChannelSource extends Source {

    public ChannelHandlerContext getChannel() {
        return channel;
    }

    public void setChannel(ChannelHandlerContext channel) {
        this.channel = channel;
    }

    private ChannelHandlerContext channel;

    public ChannelSource(ChannelHandlerContext channel){
        this.channel = channel;
    }


    @Override
    public boolean equals(Object obj) {
        ChannelSource channelSource = (ChannelSource)obj;
        return this.channel.equals(channelSource.getChannel());
    }
}
