package com.ying.cloud.lycoin.net;

import com.google.gson.Gson;
import com.ying.cloud.lycoin.models.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.List;

public class MessageEncoder extends MessageToMessageEncoder<Message> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> out) {

        try{
            ByteArrayOutputStream outputStream =new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(outputStream);
            oos.writeObject(msg);
            oos.close();

            ByteBuf buf = Unpooled.buffer();
            buf.writeBytes(outputStream.toByteArray());
            out.add(buf);
        }
        catch (Exception error){
            System.err.println(error.getMessage());
        }
    }
}
