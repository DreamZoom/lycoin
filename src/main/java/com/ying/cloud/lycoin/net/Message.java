package com.ying.cloud.lycoin.net;

import java.io.Serializable;

public class Message implements Serializable {

    public Message(){
        setId("M"+System.currentTimeMillis());
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id;

}
