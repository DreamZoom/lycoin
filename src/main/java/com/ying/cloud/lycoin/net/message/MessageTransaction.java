package com.ying.cloud.lycoin.net.message;

import com.ying.cloud.lycoin.transaction.ITransaction;

public class MessageTransaction extends  Message {
    public ITransaction getTransaction() {
        return transaction;
    }

    public void setTransaction(ITransaction transaction) {
        this.transaction = transaction;
    }

    public MessageTransaction(ITransaction transaction) {
        this.transaction = transaction;
    }

    private ITransaction transaction;
}
