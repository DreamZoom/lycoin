package com.ying.cloud.lycoin.transaction;

import java.util.ArrayList;
import java.util.List;

public class TransactionStore {

    public TransactionStore(){
        transactions =new ArrayList<>();
    }

    public synchronized List<ITransaction> getTransactions() {
        return transactions;
    }

    public synchronized void setTransactions(List<ITransaction> transactions) {
        this.transactions = transactions;
    }

    private List<ITransaction> transactions;

    public synchronized void addTransaction(ITransaction transaction){
        transactions.add(transaction);
    }
}
