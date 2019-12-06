package com.ying.cloud.lycoin.net.message;

import com.ying.cloud.lycoin.models.BlockChain;
import com.ying.cloud.lycoin.models.Message;

public class MessageChain extends Message {
    public BlockChain getChain() {
        return chain;
    }

    public void setChain(BlockChain chain) {
        this.chain = chain;
    }

    private BlockChain chain;
}
