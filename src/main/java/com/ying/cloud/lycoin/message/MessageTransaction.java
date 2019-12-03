package com.ying.cloud.lycoin.message;

import com.ying.cloud.lycoin.transaction.Transaction;

public class MessageTransaction extends  Message {
    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public MessageTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    private Transaction transaction;
}
