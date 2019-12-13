package com.ying.cloud.lycoin.net;

/**
 * 消息处理器
 */
public interface IMessageHandler<TSource extends Source> {
    void handle(TSource source, Message message);
}
