package com.ying.cloud.lycoin.message;

import com.ying.cloud.lycoin.transaction.ITransaction;
import com.ying.cloud.lycoin.transaction.Transaction;

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
