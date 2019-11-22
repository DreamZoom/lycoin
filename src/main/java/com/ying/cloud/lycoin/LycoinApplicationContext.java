package com.ying.cloud.lycoin;

import com.ying.cloud.lycoin.crypto.HashEncoder;
import com.ying.cloud.lycoin.event.LycoinEventManager;
import com.ying.cloud.lycoin.models.BlockChain;

/**
 * 区块链上下文
 */
public class LycoinApplicationContext {

    /**
     * 本地区块链
     */
    private BlockChain chain;

    /**
     * 本地配置文件
     */
    private LycoinConfig config;


    public BlockChain getChain() {
        return chain;
    }

    public void setChain(BlockChain chain) {
        this.chain = chain;
    }

    public LycoinConfig getConfig() {
        return config;
    }

    public void setConfig(LycoinConfig config) {
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


    public LycoinEventManager getEventManager() {
        return eventManager;
    }

    public void setEventManager(LycoinEventManager eventManager) {
        this.eventManager = eventManager;
    }

    /**
     * 事件管理器
     */
    private LycoinEventManager eventManager;

}
