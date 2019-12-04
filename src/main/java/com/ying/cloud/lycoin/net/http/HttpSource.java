package com.ying.cloud.lycoin.net.http;

import com.ying.cloud.lycoin.net.IMessage;
import com.ying.cloud.lycoin.net.ISource;
import com.ying.cloud.lycoin.utils.ByteArrayUtils;
import com.ying.cloud.lycoin.utils.HttpUtils;

import java.util.HashMap;
import java.util.Map;

public class HttpSource implements ISource {

    protected String host;

    public HttpSource(String host, Integer port) {
        this.host = host;
        this.port = port;
    }

    protected Integer port;

    public HttpSource(String host, Integer port, String name) {
        this.host = host;
        this.port = port;
        this.name = name;
    }

    private String name;

    @Override
    public void send(IMessage message) {
        byte[] bytes = ByteArrayUtils.encode(message);
        String msg = org.apache.commons.codec.binary.Base64.encodeBase64String(bytes);
        String url  = "http://"+host+":"+port+"?action=message";
        Map<String,String> data = new HashMap<>();
        data.put("message",msg);
        HttpUtils.doPost(url,data);
    }
}
