package com.ying.cloud.lycoin;


import com.ying.cloud.lycoin.message.MessageBlock;
import com.ying.cloud.lycoin.message.MessageChain;
import com.ying.cloud.lycoin.message.MessageHandler;
import com.ying.cloud.lycoin.message.MessageRequestBlock;
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
                if(context.getChain().accept(block)){
                    System.out.println(message.toString());
                    message.setTag("broadcast");
                    network.broadcast(message);
                }

            }
        });

        this.handler(new MessageHandler<MessageRequestBlock>() {
            @Override
            public void handle(IPeerNetwork network, MessageRequestBlock message) {
                System.out.println(message.toString());
                String hash = message.getHash();
                Block block = context.getChain().findBlock(hash);
                if(block!=null){
                    MessageBlock messageBlock =new MessageBlock("broadcast",block);
                    network.broadcast(messageBlock);
                }
            }
        });
    }
}
