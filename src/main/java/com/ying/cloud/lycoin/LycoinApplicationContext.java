package com.ying.cloud.lycoin;

import com.ying.cloud.lycoin.crypto.HashEncoder;
import com.ying.cloud.lycoin.event.LycoinEvent;
import com.ying.cloud.lycoin.event.LycoinEventListener;
import com.ying.cloud.lycoin.event.LycoinEventManager;
import com.ying.cloud.lycoin.models.BlockChain;
import com.ying.cloud.lycoin.net.PeerNetwork;

import java.util.function.Consumer;


/**
 * 区块链上下文
 */
public class LycoinApplicationContext {

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


    public void addEventListener(LycoinEventListener listener){
        eventManager.addEventListener(listener);
    }

    public void removeEventListener(LycoinEventListener listener){
        eventManager.removeEventListener(listener);
    }

    public void addEventListener(String event, Consumer<LycoinEvent> handler){
        LycoinEventListener listener = new LycoinEventListener() {
            @Override
            public String name() {
                return event;
            }

            @Override
            public void action(LycoinEvent event) {
                handler.accept(event);
            }
        };
        addEventListener(listener);
    }

    public void fireEvent(String event,Object ...data){
        eventManager.fireEvent(event,data);
    }


    public PeerNetwork getNetwork() {
        return network;
    }

    public void setNetwork(PeerNetwork network) {
        this.network = network;
    }

    private PeerNetwork network;

}
