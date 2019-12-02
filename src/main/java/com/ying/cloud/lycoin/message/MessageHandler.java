package com.ying.cloud.lycoin.message;

import com.ying.cloud.lycoin.net.IPeerNetwork;
import io.netty.channel.ChannelHandlerContext;

public interface MessageHandler<TMessage> {
    void handle(IPeerNetwork network,IMessageSource source, TMessage message);
}
