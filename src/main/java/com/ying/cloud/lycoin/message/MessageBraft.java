package com.ying.cloud.lycoin.message;

public class MessageBraft extends  Message {
    public Object getBraft() {
        return braft;
    }

    public void setBraft(Object braft) {
        this.braft = braft;
    }

    public MessageBraft(Object braft) {
        this.braft = braft;
    }

    private Object braft;
}
