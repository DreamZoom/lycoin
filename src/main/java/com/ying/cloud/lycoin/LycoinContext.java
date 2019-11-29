package com.ying.cloud.lycoin;

import com.ying.cloud.lycoin.crypto.HashEncoder;
import com.ying.cloud.lycoin.event.LycoinEvent;
import com.ying.cloud.lycoin.event.LycoinEventListener;
import com.ying.cloud.lycoin.event.LycoinEventManager;
import com.ying.cloud.lycoin.message.MessageHandler;
import com.ying.cloud.lycoin.models.BlockChain;
import com.ying.cloud.lycoin.net.IPeerNetwork;

import java.util.List;
import java.util.function.Consumer;


/**
 * 区块链上下文
 */
public class LycoinContext {

    /**
     * 本地区块链
     */
    private BlockChain chain;


    public Boolean replace(BlockChain newChain){

        return  chain.replace(newChain);
    }

    /**
     * 本地配置文件
     */
    private BlockConfig config;


    public BlockChain getChain() {
        return chain;
    }

    public void setChain(BlockChain chain) {
        this.chain = chain;
    }

    public BlockConfig getConfig() {
        return config;
    }

    public void setConfig(BlockConfig config) {
        this.config = config;
    }

    public HashEncoder getEncoder() {
        return encoder;
    }

    public void setEncoder(HashEncoder encoder) {
        this.encoder = encoder;
    }

    /**
     * Hash 编码器
     */
    private HashEncoder encoder;



    public IPeerNetwork getNetwork() {
        return network;
    }

    public void setNetwork(IPeerNetwork network) {
        this.network = network;
    }

    private IPeerNetwork network;


    public List<MessageHandler> getHandlers() {
        return handlers;
    }

    public void setHandlers(List<MessageHandler> handlers) {
        this.handlers = handlers;
    }

    private List<MessageHandler> handlers;

}