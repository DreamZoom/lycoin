package com.ying.cloud.lycoin.event;

public interface IEventListener<TEvent> {
    void handle(TEvent event);
}
