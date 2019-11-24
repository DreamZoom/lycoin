package com.ying.cloud.lycoin.event;

import java.util.EventListener;

public interface LycoinEventListener extends EventListener {
    String name();
    void action(LycoinEvent event);
}
