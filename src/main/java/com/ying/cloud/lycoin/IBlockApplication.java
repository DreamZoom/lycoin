package com.ying.cloud.lycoin;

import com.ying.cloud.lycoin.net.message.MessageHandler;

public interface IBlockApplication {

    void init(LycoinContext context);
    void setup();
    void run();
    void handler(MessageHandler handler);
}
