package com.ying.cloud.lycoin.config;

import java.io.Serializable;

public class Peer implements Serializable {
    public Peer(){

    }

    public Peer(String ip, int serverPort) {
        this.ip = ip;
        this.serverPort = serverPort;
    }

    private String ip;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }


    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    private int serverPort;

    public int getRpcPort() {
        return rpcPort;
    }

    public void setRpcPort(int rpcPort) {
        this.rpcPort = rpcPort;
    }

    private int rpcPort;

    public int getHttpPort() {
        return httpPort;
    }

    public void setHttpPort(int httpPort) {
        this.httpPort = httpPort;
    }

    private int httpPort;

}
