package com.ying.cloud.lycoin.config;

import com.google.gson.Gson;
import com.ying.cloud.lycoin.models.Account;
import com.ying.cloud.lycoin.transaction.TransactionStore;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;

/**
 * 统一配置文件
 */
public class BlockConfig {

    private static final String DEFAULT_CONFIG_FILE  = "lycoin.conf";
    public static BlockConfig load(){
        try{

            Gson gson = new Gson();
            String json =  FileUtils.readFileToString(new File(DEFAULT_CONFIG_FILE), Charset.forName("utf-8"));
            BlockConfig config=gson.fromJson(json,BlockConfig.class);
            return config;
        }
        catch (Exception error){
            System.out.println(error.getMessage());
        }
        return null;
    }


    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    private String ip;

    /**
     * 服务提供端口
     */
    private Integer serverPort;


    /**
     * RPC 通信端口
     */
    private Integer rpcPort;


    /**
     *  Restful 服务端口
     */
    private Integer httpPort;


    /**
     * Peers 列表
     */
    private List<Peer> peers;



    public Integer getServerPort() {
        return serverPort;
    }

    public void setServerPort(Integer serverPort) {
        this.serverPort = serverPort;
    }

    public Integer getRpcPort() {
        return rpcPort;
    }

    public void setRpcPort(Integer rpcPort) {
        this.rpcPort = rpcPort;
    }

    public Integer getHttpPort() {
        return httpPort;
    }

    public void setHttpPort(Integer httpPort) {
        this.httpPort = httpPort;
    }

    public List<Peer> getPeers() {
        return peers;
    }

    public void setPeers(List<Peer> peers) {
        this.peers = peers;
    }


}
