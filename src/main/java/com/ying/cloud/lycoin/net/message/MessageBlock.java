package com.ying.cloud.lycoin.net.message;

import com.ying.cloud.lycoin.models.Block;
import com.ying.cloud.lycoin.models.Message;

public class MessageBlock extends Message {
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

    private String tag;

    public MessageBlock(){

    }

    public MessageBlock(String tag){
        this.tag = tag;
    }

    public MessageBlock(String tag,Block block){
        this.tag = tag;
        this.block = block;
    }

    @Override
    public String toString() {
        StringBuffer sb =new StringBuffer();
        sb.append(tag);
        sb.append(" ");
        sb.append(block.getIndex());
        sb.append(" ");
        sb.append(block.getIp());

        return sb.toString();
    }
}
