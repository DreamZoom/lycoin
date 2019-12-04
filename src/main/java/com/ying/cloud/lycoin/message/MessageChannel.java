package com.ying.cloud.lycoin.message;

import io.netty.channel.Channel;

public class MessageChannel extends Message {
    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public MessageChannel(Channel channel) {
        this.channel = channel;
    }

    private Channel channel;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public MessageChannel(Channel channel, String tag) {
        this.channel = channel;
        this.tag = tag;
    }

    private String tag;

}
