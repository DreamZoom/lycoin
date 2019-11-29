package com.ying.cloud.lycoin.models;

import java.util.ArrayList;
import java.util.List;

public class Branch {
    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    private String head;

    public String getTail() {
        return tail;
    }

    public void setTail(String tail) {
        this.tail = tail;
    }

    private String tail;


    public List<Block> getBlocks() {
        return blocks;
    }

    public void setBlocks(List<Block> blocks) {
        this.blocks = blocks;
    }

    List<Block> blocks;

    public Branch(){
        this.blocks =new ArrayList<>();
    }

    public void addBlock(Block block){
        if(this.head==null){
            head = block.getPreviousHash();
        }
        if(this.tail==null){
            tail = block.getHash();
        }

        this.blocks.add(block);
    }

    public void addheadBlock(Block block){
        this.head=block.getPreviousHash();
        this.blocks.add(0,block);
    }

    public Block getRoot(){
        if(blocks.size()<=0) {
            return null;
        }
        return blocks.get(0);
    }

    public Block getLast(){
        if(blocks.size()<=0) {
            return null;
        }
        return blocks.get(blocks.size()-1);
    }

    public boolean hasBlock(String hash){
        for (int i = 0; i <blocks.size() ; i++) {
            if(blocks.get(i).getHash().equals(hash)){
                return  true;
            }
        }

        return  false;
    }
}
