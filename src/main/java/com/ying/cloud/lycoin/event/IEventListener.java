package com.ying.cloud.lycoin.event;

@FunctionalInterface
public interface IEventListener<TEvent> {
    void handle(Object source, TEvent event);
}
