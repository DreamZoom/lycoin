package com.ying.cloud.lycoin.message;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

public class ChannelMessageSource extends MessageSource {

    public ChannelHandlerContext getChannel() {
        return channel;
    }

    public void setChannel(ChannelHandlerContext channel) {
        this.channel = channel;
    }

    private ChannelHandlerContext channel;

    public ChannelMessageSource(ChannelHandlerContext channel){
        this.channel = channel;
    }

    @Override
    public void send(Message message) {
        channel.channel().writeAndFlush(message);
    }
}
