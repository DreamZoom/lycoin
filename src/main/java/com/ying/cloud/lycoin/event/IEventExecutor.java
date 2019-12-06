package com.ying.cloud.lycoin.event;

import java.util.function.Consumer;
import java.util.function.Function;

public interface IEventExecutor {
    void addEventListener(IEventListener listener);
    void removeEventListener(IEventListener listener);
    void dispatch(IEvent event);
}
