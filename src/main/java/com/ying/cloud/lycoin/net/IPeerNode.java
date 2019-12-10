package com.ying.cloud.lycoin.net;

public interface IPeerNode<TSource extends Source> {

    void setup();
    void broadcast(Message message);
    void send(TSource source, Message message);
    void addSource(TSource source);
    void removeSource(TSource source);
    TSource getSource(String id);
}
