package com.ying.cloud.lycoin.miner;

import com.ying.cloud.lycoin.event.Event;
import com.ying.cloud.lycoin.event.IEventSource;
import com.ying.cloud.lycoin.transaction.ITransaction;

public class TransactionEvent extends Event {
    public TransactionEvent(IEventSource source, ITransaction transaction) {
        super(source);
        this.transaction = transaction;
    }

    public ITransaction getTransaction() {
        return transaction;
    }

    private ITransaction transaction;
}
