package com.ying.cloud.lycoin.models;

import com.ying.cloud.lycoin.LycoinContext;
import com.ying.cloud.lycoin.crypto.SHA256;
import com.ying.cloud.lycoin.merkle.MerkleNode;
import com.ying.cloud.lycoin.merkle.MerkleUtils;
import com.ying.cloud.lycoin.transaction.Transaction;
import com.ying.cloud.lycoin.transaction.TransactionCoin;
import com.ying.cloud.lycoin.transaction.TransactionUtils;
import org.apache.commons.codec.binary.BinaryCodec;
import org.apache.commons.codec.binary.Hex;

import java.net.InetAddress;
import java.util.*;
import java.util.function.Function;

public class BlockChain {
    private static final int BLOCK_GENERATION_INTERVAL  = 60*1000;
    private static final int DIFFICULTY_ADJUSTMENT_INTERVAL = 10;


    private Block root;
    private  List<Branch> branches;

    private HashMap<String,Block> acceptedBlocks;
    public  BlockChain(){

        root = createRoot();
        branches = new ArrayList<>();
        acceptedBlocks = new HashMap<>();
    }

    private synchronized Block createRoot(){
        Block block =new Block();
        block.setIndex(1L);
        block.setPreviousHash("");
        block.setData("hello word");
        block.setDifficulty(10L);
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

    public synchronized long getDifficulty(){
        Block last = getBestLastBlock();
        if(last.getIndex()!=0&&last.getIndex()%DIFFICULTY_ADJUSTMENT_INTERVAL==0){
            Block prevAdjustmentBlock = findPrevByN(DIFFICULTY_ADJUSTMENT_INTERVAL,last);
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

    private synchronized Block findPrevByN(int n,Block block){
        Block prev =block;
        for (int i = 0; i < n; i++) {
            prev = findBlock(block.getPreviousHash());
            if(prev==null) return null;
        }
        return prev;
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
        return root;
    }

    public synchronized List<Block> getBlocks(){
        List<Block> blocks =new ArrayList<>();

        Block best = getBestLastBlock();

        if(best.getIndex()==1){
            blocks.add(0,root);
            return blocks;
        }

        do {
            blocks.add(0,best);
            best=findBlock(best.getPreviousHash());
        }while (best!=null);


        blocks.add(0,root);
        return blocks;
    }

    public MerkleNode getDataHash(LycoinContext context){
        TransactionCoin transaction = TransactionUtils.base(context.getAccount());
        //transaction.sign(context.getAccount());

        List<Transaction> transactions = context.getTransactions().getTransactions();
        List<MerkleNode> nodes = new ArrayList<>();
        nodes.add(transaction.getMerkleNode());

        for (int i = 0; i < transactions.size(); i++) {
            nodes.add(transactions.get(i).getMerkleNode());
        }
        MerkleNode node = MerkleUtils.tree(nodes);
        node.encode();



        return node;
    }

    public Block getNextBlock(LycoinContext context){
        String ipAddress="";
        try {
            InetAddress address = InetAddress.getLocalHost();
            ipAddress= address.getHostAddress();

        }
        catch (Exception err){}

        MerkleNode rootNode = getDataHash(context);
        Block last = getBestLastBlock();
        Block block =new Block();
        block.setIndex(last.getIndex()+1);
        block.setPreviousHash(last.getHash());
        block.setData(rootNode.getHash());
        block.setIp(ipAddress);
        block.setTimestamp(System.currentTimeMillis());
        block.setNonce(1L);

        block.setBody(rootNode);

        long difficulty = getDifficulty();
        block.setDifficulty(difficulty);

        String hash = calculateHash(block);
        block.setHash(hash);
        context.getTransactions().clearTransaction();
        return block;
    }

    public void findNextBlock(LycoinContext context,Function<Block, Boolean> callback){

        String ipAddress="";
        try {
            InetAddress address = InetAddress.getLocalHost();
            ipAddress= address.getHostAddress();

        }
        catch (Exception err){}





        long nonce = 1;
        while (true) {

            MerkleNode rootNode = getDataHash(context);

            Block last = getBestLastBlock();
            Block block =new Block();
            block.setIndex(last.getIndex()+1);
            block.setPreviousHash(last.getHash());
            block.setData(rootNode.getHash());
            block.setIp(ipAddress);
            block.setTimestamp(System.currentTimeMillis());
            block.setNonce(nonce);

            block.setBody(rootNode);

            long difficulty = getDifficulty();
            block.setDifficulty(difficulty);

            String hash = calculateHash(block);
            if (isHashMatchesDifficulty(hash, difficulty)) {
                block.setHash(hash);
                context.getTransactions().clearTransaction();
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
        if(!block.getHash().equals(hash)){
            return  false;
        }

        if(!validTimestamp(prev,block)) {
            return false;
        }


        return true;
    }
    public synchronized static boolean validTimestamp(Block prev,Block block){

        long diff = 60*1000L;
        long current = System.currentTimeMillis();

        return ( block.getTimestamp() - diff <current ) && ( prev.getTimestamp() - diff < block.getTimestamp() );
    }

    public synchronized  int size(){
        Block block = getBestLastBlock();
        if(block!=null){
            return  block.getIndex().intValue();
        }
        return 1;
    }



    public synchronized boolean accept(Block block){

        if(block.getIndex()==1) return false;

        if(acceptedBlocks.containsKey(block.getHash())) return  false;

        acceptedBlocks.put(block.getHash(),block);

        boolean isAccept =false;

        for (int i = 0; i < branches.size(); i++) {
            Branch branch = branches.get(i);
            Block last = branch.getLast();
            if(BlockChain.validBlock(last,block)){
                branch.addBlock(block);
                isAccept = true;
                break;
            }
            if(branch.getHead().equals(block.getHash())){
               Block root = branch.getRoot();
               if(BlockChain.validBlock(block,root)){
                   branch.addheadBlock(block);
                   isAccept = true;
                   break;
               }
            }
        }

        if(isAccept) return  true;

        /**
         * 没有被现有分支接受，需要创建新分支。
         */

        Branch newBranch = new Branch();
        newBranch.addBlock(block);
        branches.add(newBranch);

        return  true;
    }


    public synchronized Block getBestLastBlock(){
        Block best = getRoot();
        for (int i = 0; i < branches.size(); i++) {
            Branch branch = branches.get(i);

            /**
             * 检测分支完整性
             */
            if(checkBranch(branch)){
                Block last = branch.getLast();
                if(last.getIndex()>best.getIndex()){
                    best = last;
                }
            }

        }
        return best;
    }


    public String getLoseBlock(){
        List<Branch> loseBranchs = new ArrayList<>();
        for (int i = 0; i < branches.size(); i++) {
            Branch branch = branches.get(i);

            /**
             * 检测分支完整性
             */
            if(!checkBranch(branch)){
                loseBranchs.add(branch);
            }
        }

        if(loseBranchs.size()==0){
            return null;
        }

        Random r = new Random();
        int k = r.nextInt(loseBranchs.size());

        return  loseBranchs.get(k).getHead();
    }


    public synchronized  Block findBlock(String hash){
        if(acceptedBlocks.containsKey(hash)){
            return  acceptedBlocks.get(hash);
        }
        return null;
    }



    public synchronized boolean  checkBranch(Branch branch){
        if(branch.getHead().equals(root.getHash())){
            return  true;
        }
        for (int i = 0; i < branches.size(); i++) {
            if(branches.get(i).hasBlock(branch.getHead())){
                return true;
            }
        }
        return false;
    }

}
