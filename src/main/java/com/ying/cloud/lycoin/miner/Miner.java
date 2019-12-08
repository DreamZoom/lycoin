package com.ying.cloud.lycoin.miner;

import com.ying.cloud.lycoin.event.Emiter;
import com.ying.cloud.lycoin.merkle.MerkleNode;
import com.ying.cloud.lycoin.merkle.MerkleUtils;
import com.ying.cloud.lycoin.models.Block;
import com.ying.cloud.lycoin.models.BlockChain;
import com.ying.cloud.lycoin.transaction.Transaction;
import com.ying.cloud.lycoin.transaction.TransactionStore;

import java.util.ArrayList;
import java.util.List;

public abstract class Miner extends Emiter implements IMiner {

    protected TransactionStore transactions;

    public BlockChain getChain() {
        return chain;
    }

    protected BlockChain chain;

    public void setAdapter(IMinerEventAdapter adapter) {
        this.adapter = adapter;
    }

    protected IMinerEventAdapter adapter;

    public Miner(){
        transactions = new TransactionStore();
        chain = new BlockChain();
    }

    @Override
    public abstract void run();

    @Override
    public boolean condition() {
        return true;
    }

    @Override
    public MerkleNode pack() {
        List<MerkleNode> nodes = new ArrayList<>();
        for (int i = 0; i < transactions.getTransactions().size(); i++) {
            nodes.add(transactions.getTransactions().get(i).getMerkleNode());
        }
        MerkleNode node = MerkleUtils.tree(nodes);
        node.encode();
        return node;
    }

    @Override
    public boolean accept(Block block) {
        return chain.accept(block);
    }

    @Override
    public boolean accept(Transaction transaction) {
        return transactions.addTransaction(transaction);
    }


}
