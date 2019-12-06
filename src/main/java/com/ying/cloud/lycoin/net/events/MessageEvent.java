package com.ying.cloud.lycoin.net.events;

import com.ying.cloud.lycoin.event.Event;
import com.ying.cloud.lycoin.event.EventSource;
import com.ying.cloud.lycoin.event.IEventSource;

public class MessageEvent<TMessage> extends Event {


    public TMessage getMessage() {
        return message;
    }

    public MessageEvent(IEventSource source, TMessage message) {
        super(source);
        this.message = message;
    }

    private TMessage message;
}
