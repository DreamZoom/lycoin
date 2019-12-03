package com.ying.cloud.lycoin.transaction;

import java.io.Serializable;

public class TransactionOut implements Serializable {
    private String address;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public TransactionOut(String address, Long amount) {
        this.address = address;
        this.amount = amount;
        this.timestamp = System.currentTimeMillis();
    }

    public TransactionOut() {
        this.timestamp = System.currentTimeMillis();
    }

    private Long amount;

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    private Long timestamp;
}
