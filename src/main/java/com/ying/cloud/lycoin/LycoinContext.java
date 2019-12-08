package com.ying.cloud.lycoin;

import com.ying.cloud.lycoin.config.BlockConfig;
import com.ying.cloud.lycoin.miner.IMiner;
import com.ying.cloud.lycoin.models.Account;
import com.ying.cloud.lycoin.net.INetwork;
import com.ying.cloud.lycoin.transaction.TransactionStore;

import java.util.ArrayList;
import java.util.List;


/**
 * 区块链上下文
 */
public class LycoinContext {

    public  LycoinContext(){
        networks = new ArrayList<>();
    }

    /**
     * 本地配置文件
     */
    private BlockConfig config;


    public BlockConfig getConfig() {
        return config;
    }

    public void setConfig(BlockConfig config) {
        this.config = config;
    }








    public TransactionStore getTransactions() {
        return transactions;
    }

    public void setTransactions(TransactionStore transactions) {
        this.transactions = transactions;
    }

    private TransactionStore transactions;

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    private Account account;

    public IMiner getMiner() {
        return miner;
    }

    public void setMiner(IMiner miner) {
        this.miner = miner;
    }

    private IMiner miner;

    public List<INetwork> getNetworks() {
        return networks;
    }

    public void setNetworks(List<INetwork> networks) {
        this.networks = networks;
    }

    private List<INetwork> networks;


    public void addNetwork(INetwork network){
        networks.add(network);
    }

}
