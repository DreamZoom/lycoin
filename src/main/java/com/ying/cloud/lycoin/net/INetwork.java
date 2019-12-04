package com.ying.cloud.lycoin.net;


public interface INetwork {
    void setup();
    void broadcast(IMessage message);
    void sendMessage(ISource source, IMessage message);
    void handler(IMessageHandler messageHandler);
}
