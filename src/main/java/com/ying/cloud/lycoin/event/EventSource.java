package com.ying.cloud.lycoin.event;

public class EventSource implements IEventSource {
    public Object getSource() {
        return source;
    }

    public EventSource(Object source) {
        this.source = source;
    }

    public EventSource() {
        this.source = null;
    }

    private Object source;
}
