package com.ying.cloud.lycoin.net.messages;

import com.ying.cloud.lycoin.net.Message;

public class MsgRequestBlock extends Message {
    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public MsgRequestBlock(String hash) {
        this.hash = hash;
    }

    private String hash;
}
