package com.ying.cloud.lycoin.merkle;

import com.ying.cloud.lycoin.transaction.Transaction;

import java.util.List;
import java.util.function.Function;

public interface IMerkleNode {

    String toHashString();
    boolean verify();
    void encode();

    <T> List<T> map(Function<Transaction,T> callback);
}
