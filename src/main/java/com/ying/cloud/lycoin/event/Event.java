package com.ying.cloud.lycoin.event;

public class Event<TEventType>  implements IEvent<TEventType> {
    public IEventSource getSource() {
        return source;
    }

    public void setSource(IEventSource source) {
        this.source = source;
    }

    public Event(IEventSource source) {
        this.source = source;
    }

    private IEventSource source;

    public Event(IEventSource source, TEventType data) {
        this.source = source;
        this.data = data;
    }

    private TEventType data;

    @Override
    public TEventType getData() {
        return data;
    }

    @Override
    public Class getTypeClass() {
        return  data.getClass();
    }
}
