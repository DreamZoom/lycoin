package com.ying.cloud.lycoin.net.messages;

import com.ying.cloud.lycoin.models.Block;
import com.ying.cloud.lycoin.net.Message;

public class MsgBlock extends Message {
    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    private Block block;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public MsgBlock(Block block, String tag) {
        this.block = block;
        this.tag = tag;
    }

    private String tag;
}
