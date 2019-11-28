package com.ying.cloud.lycoin;


import com.ying.cloud.lycoin.message.MessageBlock;
import com.ying.cloud.lycoin.message.MessageChain;
import com.ying.cloud.lycoin.message.MessageHandler;
import com.ying.cloud.lycoin.models.Block;
import com.ying.cloud.lycoin.models.BlockChain;
import com.ying.cloud.lycoin.net.IPeerNetwork;


/**
 * Lycoin 币应用容器
 */
public class LycoinApplication extends BlockApplication {


    @Override
    public void init(LycoinContext context) {
        this.handler(new MessageHandler<MessageBlock>() {
            @Override
            public void handle(IPeerNetwork network, MessageBlock message) {
                Block block = message.getBlock();
                Block last = context.getChain().getLast();
                if(BlockChain.validBlock(last,block)){
                    context.getChain().addBlock(block);
                    System.out.println(message.getTag()+" a block");
                    block.print();
                    message.setTag("broadcast");
                    network.broadcast(message);
                }
            }
        });

        this.handler(new MessageHandler<MessageChain>() {
            @Override
            public void handle(IPeerNetwork network, MessageChain message) {
                BlockChain chain = message.getChain();
                if(chain.valid()){
                    if(context.replace(chain)){
                        System.out.println("accept a chain");
                        //network.broadcast(message);
                    }
                }
            }
        });
    }
}
