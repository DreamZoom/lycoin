package com.ying.cloud.lycoin.net.netty;

import com.ying.cloud.lycoin.net.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.List;

public class MessageDecoder extends MessageToMessageDecoder<ByteBuf> {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {

        try{
            int size =msg.readableBytes();
            byte[] bytes = new byte[size];
            msg.getBytes(0,bytes);
            ByteArrayInputStream inputStream =new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(inputStream);
            Message message = (Message)ois.readObject();
            ois.close();
            out.add(message);

        }
        catch (Exception err){
            System.out.println(err.getMessage());
        }


    }
}
