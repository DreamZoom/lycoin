package com.ying.cloud.lycoin.net;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ying.cloud.lycoin.models.Block;
import com.ying.cloud.lycoin.models.BlockChain;
import com.ying.cloud.lycoin.models.Message;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.lang.reflect.Type;
import java.util.List;

@ChannelHandler.Sharable
public class PeerManager extends ChannelInboundHandlerAdapter {

    public static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    BlockChain chain;
    public PeerManager(BlockChain chain){
        this.chain = chain;
    }

    public void recive_message_callback(ChannelHandlerContext ctx,Message message){

        Gson gson =new Gson();
        Message msg=message;

        if(msg.getType().equals("blocks")){
            msg.setData(chain.getChain());
            msg.setType("blocks_response");
            send_message(ctx,msg);
        }
        else if(msg.getType().equals("blocks_response")){

            Object o = msg.getData();
            String arr= gson.toJson(o);
            Type jsonType = new TypeToken<List<Block>>() {}.getType();
            List<Block> blocks = gson.fromJson(arr,jsonType);
            boolean v = BlockChain.validNewChain(blocks);
            if(v){
                if(blocks.size()>chain.getChain().size() && blocks.get(0).getHash().equals(chain.getChain().get(0).getHash())){
                    synchronized(chain){
                        System.out.println("accept a chain");
                        chain.setChain(blocks);
                        chain.print();
                    }

                }
            }
        }
        else if(msg.getType().equals("block_find")){
            Object o = msg.getData();
            String arr= gson.toJson(o);
            Block block = gson.fromJson(arr,Block.class);
            Block last = chain.getLast();
            if(BlockChain.validBlock(last,block)){
                chain.addBlock(block);
                System.out.println("accept a block");
                block.print();
                this.broadcast(msg);
            }
        }
    }

    public void send_message(ChannelHandlerContext ctx,Message message){
        String msg = new Gson().toJson(message);
        ctx.writeAndFlush(message);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        channelGroup.add(ctx.channel());

        Message message =new Message();
        message.setType("blocks");
        send_message(ctx,message);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        recive_message_callback(ctx, (Message)msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //super.exceptionCaught(ctx, cause);
        System.err.println(cause.getMessage());
    }

    public void broadcast(Message message){
        channelGroup.writeAndFlush(message);
    }
}
