package com.ying.cloud.lycoin.models;

import java.io.Serializable;

public class Message implements Serializable {


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private String type;

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    private String sign;
    private Object data;

    public Message(){
    }
    public Message(String type){
        this.type = type;
    }
    public Message(String type,Object data){
        this.type = type;
        this.data=data;
    }
}
