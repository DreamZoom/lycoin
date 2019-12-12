package com.ying.cloud.lycoin.miner;

import com.ying.cloud.lycoin.models.Block;
import com.ying.cloud.lycoin.transaction.Transaction;

public interface IMinerEventAdapter {
    void onFindBlock(Block block);
    void onLoseBlock(String hash);
    void onRequestLastBlock();
    void onAcceptTransaction(Transaction transaction);
}
