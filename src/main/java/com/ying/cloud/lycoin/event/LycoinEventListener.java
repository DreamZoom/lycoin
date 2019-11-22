package com.ying.cloud.lycoin.event;

import java.util.EventListener;

public interface LycoinEventListener<TEvent> extends EventListener {
    boolean accept(LycoinEvent event);
    void action(TEvent event);
}
