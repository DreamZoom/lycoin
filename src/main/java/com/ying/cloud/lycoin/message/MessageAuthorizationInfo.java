package com.ying.cloud.lycoin.message;

import com.ying.cloud.lycoin.transaction.AuthorizationInfo;

public class MessageAuthorizationInfo extends  Message {
    public AuthorizationInfo getInfo() {
        return info;
    }

    public void setInfo(AuthorizationInfo info) {
        this.info = info;
    }

    private AuthorizationInfo info;

    public MessageAuthorizationInfo(AuthorizationInfo info){
        this.info = info;
    }
}
