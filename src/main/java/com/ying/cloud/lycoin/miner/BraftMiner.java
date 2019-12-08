package com.ying.cloud.lycoin.miner;

import com.bitnum.braft.core.BraftContext;
import com.bitnum.braft.core.NodeState;
import com.ying.cloud.lycoin.merkle.MerkleNode;
import com.ying.cloud.lycoin.models.Block;
import com.ying.cloud.lycoin.models.BlockChain;
import com.ying.cloud.lycoin.net.Message;
import com.ying.cloud.lycoin.net.IMessageHandler;

import com.ying.cloud.lycoin.utils.SystemUtils;

public class BraftMiner extends Miner implements IMessageHandler {

    BraftContext context;
    public BraftMiner(BraftContext context){
        this.context = context;
    }

    @Override
    public void run() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try{
                        String hash = chain.getLoseBlock();
                        if(hash!=null){
                            adapter.onLoseBlock(hash);
                        }
                        Thread.sleep(2000);
                    }catch (Exception err){
                        System.out.println(err.getMessage());
                    }
                }
            }
        }).start();


        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){

                    try{
                        if(condition()){
                            System.out.println("i am a loader ,i begin find block");

                            String ip = SystemUtils.getLocalAddress();
                            Block last = chain.getBestLastBlock();
                            Block block =new Block();
                            block.setIndex(last.getIndex()+1);
                            block.setPreviousHash(last.getHash());
                            block.setData(chain.getRoot().getHash());
                            block.setIp(ip);
                            block.setTimestamp(System.currentTimeMillis());
                            block.setNonce(1L);

                            MerkleNode data = pack();

                            block.setBody(data);

                            long difficulty = chain.getDifficulty();
                            block.setDifficulty(difficulty);

                            String hash = BlockChain.calculateHash(block);
                            block.setHash(hash);

                            if(accept(block)){
                                transactions.clearTransaction();
                                adapter.onFindBlock(block);
                            }
                        }
                    }
                    catch (Exception err){
                        System.out.println(err.getMessage());
                    }
                    try {
                        Thread.sleep(3000);
                    }
                    catch (Exception error){}
                }

            }
        }).start();


    }

    @Override
    public boolean condition() {
        if(context.getLeader()==null) return false;
        return transactions.getTransactions().size()>0 && context.getMy().getState().equals(NodeState.LEADER);
    }




    @Override
    public void handle(Object source, Message message) {
        if("lose".equals(message.getType())){
            Block block =  chain.findBlock(message.getData().toString());
            adapter.onFindBlock(block);
        }
    }
}