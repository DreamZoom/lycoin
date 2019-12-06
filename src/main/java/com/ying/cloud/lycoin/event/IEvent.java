package com.ying.cloud.lycoin.event;

public interface IEvent<TEventType> {
    TEventType getData();
    Class getTypeClass();
}
