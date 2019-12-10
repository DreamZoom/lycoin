package com.ying.cloud.lycoin.net.messages;

import com.ying.cloud.lycoin.net.Message;
import com.ying.cloud.lycoin.transaction.Transaction;

public class MsgTransaction extends Message {
    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public MsgTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    private Transaction transaction;
}
