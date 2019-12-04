package com.ying.cloud.lycoin.net;

public interface IMessageHandler<TMessage> {
    void handle(ISource source,TMessage message);
}
