package com.ying.cloud.lycoin.miner;

import com.bitnum.braft.core.BraftContext;
import com.bitnum.braft.core.NodeState;
import com.ying.cloud.lycoin.event.Event;
import com.ying.cloud.lycoin.event.GlobalEventExecutor;
import com.ying.cloud.lycoin.event.IEventListener;
import com.ying.cloud.lycoin.merkle.MerkleNode;
import com.ying.cloud.lycoin.merkle.MerkleUtils;
import com.ying.cloud.lycoin.models.Block;
import com.ying.cloud.lycoin.models.BlockChain;
import com.ying.cloud.lycoin.net.events.MessageEvent;
import com.ying.cloud.lycoin.net.message.MessageBlock;
import com.ying.cloud.lycoin.net.message.MessageRequestBlock;
import com.ying.cloud.lycoin.utils.SystemUtils;

import java.util.ArrayList;
import java.util.List;

public class BraftMiner extends Miner {

    BraftContext context;
    public BraftMiner(BraftContext context){
        this.context = context;

        GlobalEventExecutor.INSTANCE.addEventListener(new IEventListener<Event<MessageRequestBlock>>() {
            @Override
            public void handle(Event<MessageRequestBlock> event) {
                System.out.println("request a block id="+event.getData().getHash());

                if(event.getSource()!=null){
                   Block block =  chain.findBlock(event.getData().getHash());
                   if(block!=null){
                       MessageBlock messageBlock =new MessageBlock("find",block);
                       GlobalEventExecutor.INSTANCE.dispatch(new Event<>(event.getSource(),messageBlock));
                   }
                }


            }
        });
    }

    @Override
    public void run() throws Exception {

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try{
                        String hash = chain.getLoseBlock();
                        if(hash!=null){
                            MessageRequestBlock requestBlock =new MessageRequestBlock(hash);
                            GlobalEventExecutor.INSTANCE.dispatch(new Event<>(null,requestBlock));
                        }
                        Thread.sleep(2000);
                    }catch (Exception err){
                        System.out.println(err.getMessage());
                    }
                }
            }
        }).start();

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

                    transactions.clear();

                    MessageBlock messageBlock =new MessageBlock("find",block);
                    GlobalEventExecutor.INSTANCE.dispatch(new Event<>(null,messageBlock));
                }
            }
            catch (Exception err){
                System.out.println(err.getMessage());
            }

            Thread.sleep(3000);

        }
    }

    @Override
    public boolean condition() {
        if(context.getLeader()==null) return false;
        return transactions.size()>0 && context.getMy().getState().equals(NodeState.LEADER);
    }


}
