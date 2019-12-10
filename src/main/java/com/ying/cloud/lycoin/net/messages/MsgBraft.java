package com.ying.cloud.lycoin.net.messages;

import com.ying.cloud.lycoin.net.Message;

public class MsgBraft extends Message {
    public Object getBraft() {
        return braft;
    }

    public void setBraft(Object braft) {
        this.braft = braft;
    }

    public MsgBraft(Object braft) {
        this.braft = braft;
    }

    private Object braft;
}
