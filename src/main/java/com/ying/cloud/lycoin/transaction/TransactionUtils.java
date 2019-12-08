package com.ying.cloud.lycoin.transaction;

import com.ying.cloud.lycoin.models.Account;

public class TransactionUtils {
    private static final long COINBASE_AMOUNT =50;
    public static TransactionCoin base(Account account){

        try{
            TransactionCoin transaction =new TransactionCoin();
            transaction.setId("TX"+System.currentTimeMillis());
            TransactionOut out =new TransactionOut(account.getPublicKey(),COINBASE_AMOUNT);
            transaction.addOutput(out);
            transaction.sign(account);

            return  transaction;
        }catch (Exception error){

        }

        return null;
    }
}
