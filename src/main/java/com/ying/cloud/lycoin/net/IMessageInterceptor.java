package com.ying.cloud.lycoin.net;

public interface IMessageInterceptor {
    boolean doReceiveMessage(Source source,Message message);
    boolean doSendMessage(Source source,Message message);
}
