package com.ying.cloud.lycoin.net;

import com.ying.cloud.lycoin.message.IMessageSource;
import com.ying.cloud.lycoin.message.Message;
import com.ying.cloud.lycoin.message.MessageHandler;

public interface IPeerNetwork {
    void broadcast(Message message);
    void handler(MessageHandler handler);
    void trigger(Message message, IMessageSource source);
}
