package com.ying.cloud.lycoin.net.message;

import com.ying.cloud.lycoin.net.IPeerNetwork;

public interface MessageHandler<TMessage> {
    void handle(IPeerNetwork network,IMessageSource source, TMessage message);
}
