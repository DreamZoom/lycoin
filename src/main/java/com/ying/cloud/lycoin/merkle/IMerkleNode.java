package com.ying.cloud.lycoin.merkle;

import com.ying.cloud.lycoin.transaction.Transaction;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public interface IMerkleNode {

    String toHashString();
    boolean verify();
    void encode();
    void iterator(Consumer<IMerkleNode> consumer);
}
