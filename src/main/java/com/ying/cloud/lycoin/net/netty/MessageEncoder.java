package com.ying.cloud.lycoin.net.netty;


import com.ying.cloud.lycoin.net.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MessageEncoder extends MessageToMessageEncoder<Message> {


    public MessageEncoder() {
        this.msgIds = new ArrayList<>();
    }

    List<String> msgIds;

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> out) {

        try{


            if(msgIds.contains(msg.getId())){
                return;
            }

            msgIds.add(msg.getId());
            if(msgIds.size()>512){
                msgIds.remove(0);
            }

            ByteArrayOutputStream outputStream =new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(outputStream);
            oos.writeObject(msg);
            oos.close();

            ByteBuf buf = Unpooled.buffer();

            byte[] bytes = outputStream.toByteArray();
            buf.writeBytes(bytes);

            out.add(buf);
        }
        catch (Exception error){
            System.err.println(error.getMessage());
        }
    }
}
