package com.ying.cloud.lycoin.models;

public class MessageFindBlock extends Message {

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    private Block block;
}
