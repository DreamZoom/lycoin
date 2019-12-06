package com.ying.cloud.lycoin.miner;

import com.ying.cloud.lycoin.event.GlobalEventExecutor;
import com.ying.cloud.lycoin.merkle.MerkleNode;
import com.ying.cloud.lycoin.models.Block;
import com.ying.cloud.lycoin.models.BlockChain;
import com.ying.cloud.lycoin.net.events.MessageEvent;
import com.ying.cloud.lycoin.net.message.MessageBlock;
import com.ying.cloud.lycoin.utils.SystemUtils;

public class CoinMiner extends Miner {
    @Override
    public void run() throws Exception {
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

                    MessageBlock messageBlock =new MessageBlock("find",block);
                    MessageEvent<MessageBlock> event =new MessageEvent(null,block);
                    GlobalEventExecutor.INSTANCE.dispatch(event);
                }


            }
            catch (Exception err){
                System.out.println(err.getMessage());
            }

            Thread.sleep(3000);
        }
    }
}
