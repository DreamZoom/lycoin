package com.ying.cloud.lycoin.net;

import java.io.Serializable;

public class Message<TEntity extends Object> implements Serializable {


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id;

    public TEntity getData() {

        return data;
    }

    public void setData(TEntity data) {
        this.data = data;
    }

    public Message(TEntity data) {
        this.data = data;
    }

    private TEntity data;




    public Message(String type,TEntity data) {
        this.data = data;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private String type;


}
