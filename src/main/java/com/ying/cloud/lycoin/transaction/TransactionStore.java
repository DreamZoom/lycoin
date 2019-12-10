package com.ying.cloud.lycoin.transaction;

import com.ying.cloud.lycoin.models.Account;

import java.util.ArrayList;
import java.util.List;

public class TransactionStore {

    public TransactionStore(){
        transactions =new ArrayList<>();
        unspentTransactionOuts =new ArrayList<>();
    }

    public synchronized List<Transaction> getTransactions() {
        return transactions;
    }

    public synchronized void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    private List<Transaction> transactions;

    public synchronized boolean addTransaction(Transaction transaction){
        for (int i = 0; i < transactions.size(); i++) {
            if(transactions.get(i).getId().equals(transaction.getId())) return false;
        }
        transactions.add(transaction);
        return true;
    }

    public List<UnspentTransactionOut> getUnspentTransactionOuts() {
        return unspentTransactionOuts;
    }

    public void setUnspentTransactionOuts(List<UnspentTransactionOut> unspentTransactionOuts) {
        this.unspentTransactionOuts = unspentTransactionOuts;
    }

    private List<UnspentTransactionOut> unspentTransactionOuts;

    public synchronized void addUnspentTransactionOut(UnspentTransactionOut unspentTransactionOut){
        unspentTransactionOuts.add(unspentTransactionOut);
    }


    public synchronized void clearTransaction(){
        transactions.clear();
    }


    public synchronized void updateUnspentTransactionOut(List<TransactionCoin> transactions){

        for (int i = 0; i < transactions.size(); i++) {
            for (int j = 0; j < transactions.get(i).getInputs().size(); j++) {
                TransactionIn in = transactions.get(i).getInputs().get(j);
                unspentTransactionOuts.removeIf((v)->{
                    return v.getId().equals(in.getId()) && v.getIndex().equals(in.getIndex());
                });
            }

            for (int j = 0; j < transactions.get(i).getOutputs().size(); j++) {
                TransactionOut out = transactions.get(i).getOutputs().get(j);
                UnspentTransactionOut unspentTransactionOut =new UnspentTransactionOut();
                unspentTransactionOut.setId(transactions.get(i).getId());
                unspentTransactionOut.setIndex(j);
                unspentTransactionOut.setAddress(out.getAddress());
                unspentTransactionOut.setAmount(out.getAmount());
                unspentTransactionOuts.add(unspentTransactionOut);
            }
        }
    }


    public synchronized TransactionCoin tx(Account account, TransactionOut out) throws Exception{
        List<TransactionIn> transactionIns =new ArrayList<>();
        List<TransactionOut> outs =new ArrayList<>();
        long amount =0;
        long overAmount=0;
        for (Integer i = 0; i <unspentTransactionOuts.size() ; i++) {

           if(unspentTransactionOuts.get(i).getAddress().equals(account.getPublicKey())) {
               amount+=unspentTransactionOuts.get(i).getAmount();
               transactionIns.add(new TransactionIn(Long.parseLong(i.toString()),unspentTransactionOuts.get(i).getId()));
               if(amount>=out.getAmount()){
                   overAmount = amount-out.getAmount();
                   outs.add(out);

                   if(overAmount!=0){
                        outs.add(new TransactionOut(account.getPublicKey(),overAmount));
                   }

                   break;
               }
           }
        }

        if(amount<out.getAmount()){
            return null;
        }


        TransactionCoin transaction =new TransactionCoin();
        transaction.setInputs(transactionIns);
        transaction.setOutputs(outs);
        transaction.sign(account);

        addTransaction(transaction);

        return transaction;
    }
}
