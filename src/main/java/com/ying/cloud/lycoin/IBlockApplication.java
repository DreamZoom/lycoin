package com.ying.cloud.lycoin;

import com.ying.cloud.lycoin.message.MessageHandler;

public interface IBlockApplication {

    void init(LycoinContext context);
    void setup();
    void handler(MessageHandler handler);
}
