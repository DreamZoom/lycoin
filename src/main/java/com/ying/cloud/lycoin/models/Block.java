package com.ying.cloud.lycoin.models;

import java.io.Serializable;

public class Block implements Serializable {
    public Long getIndex() {
        return index;
    }

    public void setIndex(Long index) {
        this.index = index;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }



    public Long getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Long difficulty) {
        this.difficulty = difficulty;
    }

    public Long getNonce() {
        return nonce;
    }

    public void setNonce(Long nonce) {
        this.nonce = nonce;
    }

    private Long index;
    private String previousHash;
    private String hash;
    private String data;

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    private Long timestamp;
    private Long difficulty;
    private Long nonce;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    private String ip;


    public void print(){
        System.out.println("-------------------------");
        System.out.print("index:");
        System.out.println(index);
        System.out.print("prevHash:");
        System.out.println(previousHash);
        System.out.print("hash:");
        System.out.println(hash);
        System.out.print("data:");
        System.out.println(data);
        System.out.print("timestamp:");
        System.out.println(timestamp);
        System.out.print("difficulty:");
        System.out.println(difficulty);
        System.out.print("nonce:");
        System.out.println(nonce);
        System.out.print("ip:");
        System.out.println(ip);
        System.out.println("-------------------------");
    }

    @Override
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("index:"+index+"\n");
        stringBuffer.append("previousHash:"+previousHash+"\n");
        stringBuffer.append("hash:"+hash+"\n");
        stringBuffer.append("data:"+data+"\n");
        stringBuffer.append("timestamp:"+timestamp+"\n");
        stringBuffer.append("difficulty:"+difficulty+"\n");
        stringBuffer.append("nonce:"+nonce+"\n");
        stringBuffer.append("ip:"+ip+"\n");
        return stringBuffer.toString();
    }
}
