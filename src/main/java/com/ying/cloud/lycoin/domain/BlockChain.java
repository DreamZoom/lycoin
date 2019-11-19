package com.ying.cloud.lycoin.domain;

import com.ying.cloud.lycoin.crypto.SHA256;
import org.apache.commons.codec.binary.BinaryCodec;
import org.apache.commons.codec.binary.Hex;

import java.util.ArrayList;
import java.util.List;

public class BlockChain {
    private static final int BLOCK_GENERATION_INTERVAL  = 10;
    private static final int DIFFICULTY_ADJUSTMENT_INTERVAL = 10;

    public List<Block> getChain() {
        return chain;
    }

    public void setChain(List<Block> chain) {
        this.chain = chain;
    }

    private List<Block> chain;
    public  BlockChain(){
        chain =new ArrayList<>();
        Block root = createRoot();
        chain.add(root);
    }

    private Block createRoot(){
        Block block =new Block();
        block.setIndex(1L);
        block.setPreviousHash("");
        block.setData("hello word");
        block.setDifficulty(2L);
        block.setNonce(1L);
        block.setTimestamp(15728995444L);
        String hash = calculateHash(block);
        block.setHash(hash);
        return  block;
    }

    public static String calculateHash(Block block){
        StringBuffer sb=new StringBuffer();
        sb.append(block.getIndex());
        sb.append(block.getPreviousHash());
        sb.append(block.getData());
        sb.append(block.getTimestamp());
        sb.append(block.getDifficulty());
        sb.append(block.getNonce());
        return  SHA256.encode(sb.toString());
    }

    private long getDifficulty(){
        Block last = chain.get(chain.size()-1);
        if(last.getIndex()!=0&&last.getIndex()%DIFFICULTY_ADJUSTMENT_INTERVAL==0){
            Block prevAdjustmentBlock = chain.get(chain.size()-DIFFICULTY_ADJUSTMENT_INTERVAL);
            long timeExpected = DIFFICULTY_ADJUSTMENT_INTERVAL*BLOCK_GENERATION_INTERVAL;
            long timeTaken = last.getTimestamp()-prevAdjustmentBlock.getTimestamp();
            if(timeTaken<timeExpected/2){
               return   prevAdjustmentBlock.getDifficulty()+1;
            }else if(timeTaken>timeExpected*2){
                return   prevAdjustmentBlock.getDifficulty()-1;
            }
        }
        return last.getDifficulty();
    }

    private boolean isHashMatchesDifficulty(String hash,long difficulty){
        try{
            byte[] bytes= Hex.decodeHex(hash.toCharArray());

            String bs =  BinaryCodec.toAsciiString(bytes);
            for (int i = 0; i < difficulty; i++) {
                if(bs.charAt(i)!='0'){
                    return  false;
                }
            }

            return  true;
        }
        catch (Exception err){
        }
        return false;
    }

    public Block findNextBlock(){
        Block last = chain.get(chain.size()-1);

        Block block =new Block();
        block.setIndex(last.getIndex()+1);
        block.setPreviousHash(last.getHash());
        block.setData("hello word");

        block.setTimestamp(System.currentTimeMillis());

        long difficulty = getDifficulty();
        block.setDifficulty(difficulty);

        long nonce = 1;
        while (true) {
            block.setNonce(nonce);
            String hash = calculateHash(block);
            if (isHashMatchesDifficulty(hash, difficulty)) {
                block.setHash(hash);
                break;
            }
            nonce++;
        }

        chain.add(block);
        return  block;
    }

    public static boolean validBlock(Block prev,Block block){

        if(!block.getPreviousHash().equals(prev.getHash())) return  false;

        if(prev.getIndex()!=block.getIndex()-1){
            return  false;
        }

        String hash = calculateHash(block);
        if(!block.getHash().equals(hash)) return  false;

        if(!validTimestamp(prev,block)) return  false;


        return true;
    }
    public static boolean validTimestamp(Block prev,Block block){
        return ( block.getTimestamp() - 60 < System.currentTimeMillis())
                && ( prev.getTimestamp() - 60 < block.getTimestamp() );
    }
    public static boolean validNewChain(List<Block> chain){
        if(chain.size()<=0){
            return  false;
        }

        Block prev= chain.get(0);
        for (int i = 1; i <chain.size() ; i++) {
            Block block= chain.get(i);
            if(!validBlock(prev,block)) return false;
            prev=block;
        }

        return true;
    }



    public void print(){
        chain.forEach((block)->{block.print();});
    }
}
