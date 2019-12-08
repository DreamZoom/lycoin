package com.ying.cloud.lycoin.miner;

import com.ying.cloud.lycoin.merkle.MerkleNode;
import com.ying.cloud.lycoin.models.Block;
import com.ying.cloud.lycoin.transaction.Transaction;

/**
 * 矿机，实现不同的挖矿算法
 */
public interface IMiner {
    void run();
    boolean condition();
    MerkleNode pack();
    boolean accept(Block block);
    boolean accept(Transaction transaction);
}
