package com.ying.cloud.lycoin.miner;

import com.ying.cloud.lycoin.merkle.MerkleNode;

/**
 * 矿机，实现不同的挖矿算法
 */
public interface IMiner {
    void run() throws Exception;
    boolean condition();
    MerkleNode pack();
}
