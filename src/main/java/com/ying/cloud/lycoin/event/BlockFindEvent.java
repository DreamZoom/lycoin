package com.ying.cloud.lycoin.event;

import com.ying.cloud.lycoin.models.Block;

public class BlockFindEvent extends  LycoinEvent {

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    /**
     * this is block
     */
    private Block block;

    public BlockFindEvent(Object source) {
        super(source);
    }
}
