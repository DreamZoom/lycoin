package com.ying.cloud.lycoin.event;

import java.util.EventObject;

/**
 * 区块事件
 */
public class LycoinEvent extends EventObject {
    private static final long serialVersionUID = 6496098798146410884L;
    public LycoinEvent(Object source) {
        super(source);
    }
}
