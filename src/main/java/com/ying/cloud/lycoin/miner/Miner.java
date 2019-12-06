package com.ying.cloud.lycoin.miner;

import com.ying.cloud.lycoin.event.Event;
import com.ying.cloud.lycoin.event.GlobalEventExecutor;
import com.ying.cloud.lycoin.event.IEventListener;
import com.ying.cloud.lycoin.merkle.MerkleNode;
import com.ying.cloud.lycoin.merkle.MerkleUtils;
import com.ying.cloud.lycoin.models.Block;
import com.ying.cloud.lycoin.models.BlockChain;
import com.ying.cloud.lycoin.net.message.MessageBlock;
import com.ying.cloud.lycoin.net.message.MessageTransaction;
import com.ying.cloud.lycoin.transaction.ITransaction;
import com.ying.cloud.lycoin.transaction.Transaction;

import java.util.ArrayList;
import java.util.List;

public class Miner implements IMiner {

    protected List<ITransaction> transactions;

    public BlockChain getChain() {
        return chain;
    }

    protected BlockChain chain;

    public Miner(){
        transactions = new ArrayList<>();
        chain = new BlockChain();

        GlobalEventExecutor.INSTANCE.addEventListener(new IEventListener<Event<MessageTransaction>>() {
            @Override
            public void handle(Event<MessageTransaction> event) {
                transactions.add(event.getData().getTransaction());
            }
        });

        GlobalEventExecutor.INSTANCE.addEventListener(new IEventListener<Event<MessageBlock>>() {
            @Override
            public void handle(Event<MessageBlock> event) {
                if(chain.accept(event.getData().getBlock())){
                    System.out.println("receive a block");
                }
            }
        });
    }

    @Override
    public void run() throws Exception {

    }

    @Override
    public boolean condition() {
        return true;
    }

    @Override
    public MerkleNode pack() {
        List<MerkleNode> nodes = new ArrayList<>();
        for (int i = 0; i < transactions.size(); i++) {
            nodes.add(transactions.get(i).getMerkleNode());
        }
        MerkleNode node = MerkleUtils.tree(nodes);
        node.encode();
        return node;
    }


}
