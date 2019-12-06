package com.ying.cloud.lycoin.net.http;

import com.ying.cloud.lycoin.net.Source;

public class HttpSource extends Source {

    protected String host;

    public HttpSource(String host, Integer port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getMyPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    protected Integer port;

    public HttpSource(String host, Integer port, String name) {
        this.host = host;
        this.port = port;
        this.name = name;
    }

    private String name;

}
