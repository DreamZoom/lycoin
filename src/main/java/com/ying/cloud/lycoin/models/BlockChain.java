package com.ying.cloud.lycoin.models;

import com.ying.cloud.lycoin.crypto.SHA256;
import org.apache.commons.codec.binary.BinaryCodec;
import org.apache.commons.codec.binary.Hex;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class BlockChain {
    private static final int BLOCK_GENERATION_INTERVAL  = 60*1000;
    private static final int DIFFICULTY_ADJUSTMENT_INTERVAL = 10;

    public synchronized List<Block> getChain() {
        return chain;
    }

    public synchronized void setChain(List<Block> chain) {
        this.chain.clear();
        this.chain.addAll(chain);
    }

    private  List<Block> chain;
    public  BlockChain(){
        chain =new ArrayList<>();
        Collections.synchronizedList(chain);
        Block root = createRoot();
        chain.add(root);
    }

    private synchronized Block createRoot(){
        Block block =new Block();
        block.setIndex(1L);
        block.setPreviousHash("");
        block.setData("hello word");
        block.setDifficulty(20L);
        block.setNonce(1L);
        block.setIp("255.255.255.255");
        block.setTimestamp(15728995444L);
        String hash = calculateHash(block);
        block.setHash(hash);
        return  block;
    }

    public synchronized static String calculateHash(Block block){
        StringBuffer sb=new StringBuffer();
        sb.append(block.getIndex());
        sb.append(block.getPreviousHash());
        sb.append(block.getData());
        sb.append(block.getTimestamp());
        sb.append(block.getDifficulty());
        sb.append(block.getNonce());
        sb.append(block.getIp());
        return  SHA256.encode(sb.toString());
    }

    private synchronized long getDifficulty(){
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

    private synchronized boolean isHashMatchesDifficulty(String hash,long difficulty){
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

    public synchronized Block getRoot(){
        if(chain.size()<=0) {
            return null;
        }
        return chain.get(0);
    }
    public synchronized Block getLast(){
        if(chain.size()<=0) {
            return null;
        }
        return chain.get(chain.size()-1);
    }

    public synchronized void addBlock(Block block){
        chain.add(block);
    }

    public void findNextBlock(Function<Block,Boolean> callback){

        String ipAddress="";
        try {
            InetAddress address = InetAddress.getLocalHost();
            ipAddress= address.getHostAddress();

        }
        catch (Exception err){}

        long nonce = 1;
        while (true) {

            Block last = getLast();
            Block block =new Block();
            block.setIndex(last.getIndex()+1);
            block.setPreviousHash(last.getHash());
            block.setData("hello word");
            block.setIp(ipAddress);
            block.setTimestamp(System.currentTimeMillis());
            block.setNonce(nonce);

            long difficulty = getDifficulty();
            block.setDifficulty(difficulty);

            String hash = calculateHash(block);
            if (isHashMatchesDifficulty(hash, difficulty)) {
                block.setHash(hash);
                callback.apply(block);
                nonce=0;
            }
            nonce++;
        }
    }

    public synchronized static boolean validBlock(Block prev,Block block){

        if(!block.getPreviousHash().equals(prev.getHash())) return  false;

        if(prev.getIndex()!=block.getIndex()-1){
            return  false;
        }

        String hash = calculateHash(block);
        if(!block.getHash().equals(hash)) return  false;

        if(!validTimestamp(prev,block)) return  false;


        return true;
    }
    public synchronized static boolean validTimestamp(Block prev,Block block){
        return ( block.getTimestamp() - 60 < System.currentTimeMillis())
                && ( prev.getTimestamp() - 60 < block.getTimestamp() );
    }
    public synchronized static boolean validNewChain(List<Block> chain){
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


    public synchronized  int size(){
        return chain.size();
    }

    public synchronized boolean replace(BlockChain newChain){
        if(newChain.size()<=0) return false;
        if(this.size()>newChain.size()) return  false;

        if(!getRoot().getHash().equals(newChain.getRoot().getHash())) return false;

        setChain(newChain.getChain());
        return  true;
    }

    public boolean valid(){
        return BlockChain.validNewChain(this.chain);
    }


    public synchronized  void print(){
        chain.forEach((block)->{block.print();});
    }
}
