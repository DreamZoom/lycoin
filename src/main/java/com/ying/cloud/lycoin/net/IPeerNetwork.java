package com.ying.cloud.lycoin.net;

import com.ying.cloud.lycoin.net.message.IMessageSource;
import com.ying.cloud.lycoin.net.message.Message;
import com.ying.cloud.lycoin.net.message.MessageHandler;

public interface IPeerNetwork {
    void broadcast(Message message);
    void handler(MessageHandler handler);
    void trigger(Message message, IMessageSource source);
}
