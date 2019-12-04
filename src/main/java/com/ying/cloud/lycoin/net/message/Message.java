package com.ying.cloud.lycoin.net.message;

import com.ying.cloud.lycoin.net.IMessage;

import java.io.Serializable;

public class Message implements IMessage, Serializable {

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    private String sign;
}
