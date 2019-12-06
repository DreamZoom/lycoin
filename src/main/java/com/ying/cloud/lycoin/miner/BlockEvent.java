package com.ying.cloud.lycoin.miner;

import com.ying.cloud.lycoin.event.Event;
import com.ying.cloud.lycoin.event.IEventSource;
import com.ying.cloud.lycoin.models.Block;

public class BlockEvent extends Event {
    public Block getBlock() {
        return block;
    }

    public BlockEvent(IEventSource source, Block block) {
        super(source);
        this.block = block;
    }

    private Block block;
}
