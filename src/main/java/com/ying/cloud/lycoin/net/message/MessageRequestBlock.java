package com.ying.cloud.lycoin.net.message;

import com.ying.cloud.lycoin.models.Message;

public class MessageRequestBlock extends Message {
    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    private String hash;

    public MessageRequestBlock(String hash){
        this.hash = hash;
    }

    @Override
    public String toString() {
        return "request block "+hash;
    }
}
