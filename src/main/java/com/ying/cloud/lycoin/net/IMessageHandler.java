package com.ying.cloud.lycoin.net;

/**
 * 消息处理器
 */
public interface IMessageHandler {
    void handle(Object source, Message message);
}
