package com.ying.cloud.lycoin.net.message;

import com.ying.cloud.lycoin.net.IMessage;

import java.io.Serializable;

public class MessageBraft implements IMessage,Serializable {
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
