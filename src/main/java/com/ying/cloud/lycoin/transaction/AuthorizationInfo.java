package com.ying.cloud.lycoin.transaction;

import com.ying.cloud.lycoin.crypto.SHA256;
import com.ying.cloud.lycoin.merkle.MerkleDataNode;
import com.ying.cloud.lycoin.merkle.MerkleNode;

public class AuthorizationInfo extends MerkleDataNode implements ITransaction{
    @Override
    public String toHashString() {
        return SHA256.encode(this.from+this.to+this.system+this.username+this.seconds+this.timestamp);
    }


    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getSeconds() {
        return seconds;
    }

    public void setSeconds(Long seconds) {
        this.seconds = seconds;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    //授权人公钥
    private String from;
    //被授权人
    private String to;

    //授权的系统
    private String system;

    //授权的账号
    private String username;

    //授权的时间长度（秒）
    private Long seconds;

    //授权起始时间
    private Long timestamp;

    @Override
    public String getId() {
        return toHashString();
    }

    @Override
    public MerkleNode getMerkleNode() {
        return this;
    }
}
