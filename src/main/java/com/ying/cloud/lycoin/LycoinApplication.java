package com.ying.cloud.lycoin;


import com.ying.cloud.lycoin.merkle.MerkleNode;
import com.ying.cloud.lycoin.merkle.MerkleUtils;
import com.ying.cloud.lycoin.message.*;
import com.ying.cloud.lycoin.models.Block;
import com.ying.cloud.lycoin.net.IPeerNetwork;
import com.ying.cloud.lycoin.transaction.ITransaction;
import com.ying.cloud.lycoin.transaction.Transaction;
import com.ying.cloud.lycoin.transaction.TransactionStore;
import com.ying.cloud.lycoin.transaction.TransactionUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Lycoin 币应用容器
 */
public class LycoinApplication extends BlockApplication {


    @Override
    public void init(LycoinContext context) {
        this.handler(new MessageHandler<MessageBlock>() {
            @Override
            public void handle(IPeerNetwork network, IMessageSource source, MessageBlock message) {
                Block block = message.getBlock();
                if(context.getChain().accept(block)){

                    List<Transaction> list = block.getBody().map((iTransaction)->{
                        return (Transaction)iTransaction;
                    });

                    context.getTransactions().updateUnspentTransactionOut(list);

                    System.out.println(message.toString());
                    message.setTag("broadcast");
                    network.broadcast(message);
                }

            }
        });

        /**
         * 请求块消息处理
         */
        this.handler(new MessageHandler<MessageRequestBlock>() {
            @Override
            public void handle(IPeerNetwork network,IMessageSource source, MessageRequestBlock message) {
                System.out.println(message.toString());
                String hash = message.getHash();
                Block block = context.getChain().findBlock(hash);
                if(block!=null){
                    MessageBlock messageBlock =new MessageBlock("broadcast",block);
                    network.broadcast(messageBlock);
                }
            }
        });

        /**
         * 交易消息处理
         *
         */
        this.handler(new MessageHandler<MessageTransaction>() {
            @Override
            public void handle(IPeerNetwork network, IMessageSource source, MessageTransaction message) {
                Transaction transaction = message.getTransaction();
                if(context.getTransactions().addTransaction(transaction)) {
                    network.broadcast(message);
                }
            }
        });
    }

    @Override
    public void run() {
        context.getChain().findNextBlock(context,( block)->{
            MessageBlock messageBlock =new MessageBlock("find",block);
            context.getNetwork().trigger(messageBlock,null);
            return  true;
        });
    }
}
