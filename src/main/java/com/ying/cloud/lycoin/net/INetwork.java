package com.ying.cloud.lycoin.net;


import com.ying.cloud.lycoin.models.Message;

public interface INetwork {
    void setup();
    void broadcast(Message message);
    void sendMessage(Source source, Message message);
    void receiveMessage(Source source, Message message);
    void addSource(Source source);
    void removeSource(Source source);
}
