package com.ying.cloud.lycoin.models;

import java.io.Serializable;

public class Message implements Serializable {

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    private String sign;
}
