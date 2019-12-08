package com.ying.cloud.lycoin.miner;
import com.ying.cloud.lycoin.merkle.MerkleNode;
import com.ying.cloud.lycoin.merkle.MerkleUtils;
import com.ying.cloud.lycoin.models.Account;
import com.ying.cloud.lycoin.models.Block;
import com.ying.cloud.lycoin.models.BlockChain;
import com.ying.cloud.lycoin.net.Message;
import com.ying.cloud.lycoin.net.IMessageHandler;
import com.ying.cloud.lycoin.transaction.TransactionUtils;
import com.ying.cloud.lycoin.utils.SystemUtils;

import java.util.ArrayList;
import java.util.List;

public class CoinMiner extends Miner implements IMessageHandler {

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    private Account account;
    @Override
    public void run() {


        new Thread(new Runnable() {
            @Override
            public void run() {

                while (true){

                    try{

                        if(condition()){
                            try{
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

                            }catch (Exception error){
                                System.out.println(error.getMessage());
                            }

                        }


                    }
                    catch (Exception err){
                        System.out.println(err.getMessage());
                    }
                    finally {
                        try {
                            Thread.sleep(3000);
                        }
                        catch (Exception error){}
                    }


                }

            }
        }).start();

    }

    @Override
    public MerkleNode pack() {
        List<MerkleNode> nodes = new ArrayList<>();
        nodes.add(TransactionUtils.base(account).getMerkleNode());
        for (int i = 0; i < transactions.getTransactions().size(); i++) {
            nodes.add(transactions.getTransactions().get(i).getMerkleNode());
        }
        MerkleNode node = MerkleUtils.tree(nodes);
        node.encode();
        return node;
    }

    @Override
    public void handle(Object source, Message message) {
        System.out.println(message.getType());
    }
}
