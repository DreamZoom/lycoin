package com.ying.cloud.lycoin.event;

import java.util.EventObject;

/**
 * 区块事件
 */
public class LycoinEvent extends EventObject {
    private static final long serialVersionUID = 6496098798146410884L;

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    private String eventName;

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    private Object data;
    public LycoinEvent(Object source) {
        super(source);
    }

    public LycoinEvent(Object source,String eventName) {
        super(source);
        this.eventName =eventName;
    }
    public LycoinEvent(Object source,String eventName,Object data) {
        super(source);
        this.eventName =eventName;
        this.data = data;
    }



}
