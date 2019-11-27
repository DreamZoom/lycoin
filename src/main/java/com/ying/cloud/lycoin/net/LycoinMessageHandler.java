package com.ying.cloud.lycoin.net;

import io.netty.channel.ChannelHandlerContext;

public interface LycoinMessageHandler<TMessage> {
    void handle(ChannelHandlerContext ctx, PeerNetwork network, TMessage message);
}
