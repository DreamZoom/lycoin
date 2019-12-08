package com.ying.cloud.lycoin.miner;

import com.ying.cloud.lycoin.models.Block;

public interface IMinerEventAdapter {
    void onFindBlock(Block block);
    void onLoseBlock(String hash);

}
