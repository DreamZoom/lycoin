package com.ying.cloud.lycoin.transaction;

public class TransactionOut {
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
    }

    private Long amount;
}
