package com.ying.cloud.lycoin.transaction;

import java.io.Serializable;

public class TransactionIn implements Serializable {
    private Long index;
    private String id;
    public TransactionIn() {

    }


    public TransactionIn(Long index, String id) {
        this.index = index;
        this.id = id;
    }

    public Long getIndex() {
        return index;
    }

    public void setIndex(Long index) {
        this.index = index;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    private String signature;
}
