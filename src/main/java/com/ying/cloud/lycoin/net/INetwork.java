package com.ying.cloud.lycoin.net;


public interface INetwork {
    void setup();
    void broadcast(Message message);
    void sendMessage(Source source, Message message);
    void receiveMessage(Source source, Message message);
    void addSource(Source source);
    void removeSource(Source source);
}
