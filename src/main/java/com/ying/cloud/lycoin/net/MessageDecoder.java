package com.ying.cloud.lycoin.net;

import com.google.gson.Gson;
import com.ying.cloud.lycoin.models.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

public class MessageDecoder extends MessageToMessageDecoder<ByteBuf> {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
//        int size =msg.getInt(0);
//        byte[] bytes = new byte[size];
//        msg.getBytes(4,bytes);
        int size =msg.readableBytes();
        byte[] bytes = new byte[size];
        msg.getBytes(0,bytes);
        String json = new String(bytes);
        Gson gson = new Gson();
        Message message =gson.fromJson(json,Message.class);
        out.add(message);
    }
}
