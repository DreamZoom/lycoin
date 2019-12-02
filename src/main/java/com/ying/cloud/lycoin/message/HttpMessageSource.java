package com.ying.cloud.lycoin.message;

public class HttpMessageSource  extends  MessageSource{
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    private  String ip;

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public HttpMessageSource(String ip, Integer port) {
        this.ip = ip;
        this.port = port;
    }

    private  Integer port;
}
