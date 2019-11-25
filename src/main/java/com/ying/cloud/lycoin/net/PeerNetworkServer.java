package com.ying.cloud.lycoin.net;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ying.cloud.lycoin.LycoinApplicationContext;
import com.ying.cloud.lycoin.models.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

import java.lang.reflect.Type;
import java.util.List;

public class PeerNetworkServer {

    LycoinApplicationContext context;

    private PeerNetwork network;
    public PeerNetworkServer(LycoinApplicationContext context){
        network = new PeerNetwork(context){
            @Override
            public void onReceiveMessage(ChannelHandlerContext ctx, Message message) {
                receive_message_callback(ctx,message);
            }
        };
        this.context = context;
        context.setNetwork(network);
    }

    public void receive_message_callback(ChannelHandlerContext ctx,Message message){

        Gson gson =new Gson();
        Message msg=message;
        System.out.println(msg.getType());

        if(msg.getType().equals("blocks")){
            msg.setData(context.getChain());
            msg.setType("blocks_response");
            network.send_message(ctx,msg);
        }
        else if(msg.getType().equals("blocks_response")){
            Object o = msg.getData();
            String arr= gson.toJson(o);
            //Type jsonType = new TypeToken<List<Block>>() {}.getType();
            BlockChain newChain = gson.fromJson(arr,BlockChain.class);
            boolean v = BlockChain.validNewChain(newChain.getChain());
            if(v){
                if(context.replace(newChain)){
                    System.out.println("accept a chain");
                    context.fireEvent("replace");
                }
            }
        }
        else if(msg.getType().equals("block_find")){
            Object o = msg.getData();
            String arr= gson.toJson(o);
            Block block = gson.fromJson(arr,Block.class);
            Block last = context.getChain().getLast();
            if(BlockChain.validBlock(last,block)){
                context.getChain().addBlock(block);
                System.out.println("accept a block");
                block.print();
                network.broadcast(msg);
            }
        }
    }

    public void setup(){
        try{
            setupServer();
            setupClients();
            System.out.println("server is setup ");
        }
        catch (Exception error){

        }
    }

    public void setupServer() throws Exception {
        ServerBootstrap serverBootstrap = new ServerBootstrap();

        NioEventLoopGroup boos = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();

        serverBootstrap
                .group(boos, worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) {
                        System.out.println("accept a socket");

                        ch.config().setAllowHalfClosure(true);
                        ch.pipeline().addLast(new LengthFieldPrepender(4));
                        ch.pipeline().addLast(new MessageEncoder());

                        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024*500,0,4,0,4));
                        ch.pipeline().addLast(new MessageDecoder());
                        ch.pipeline().addLast(network);
                    }
                });


        int port = context.getConfig().getServerPort();
        serverBootstrap.bind(port);
        System.out.println("server setup at "+port);
    }

    public void setupClients(){

        Bootstrap bootstrap = new Bootstrap();
        NioEventLoopGroup group = new NioEventLoopGroup();
        bootstrap.group(group)
                .channel(NioSocketChannel.class).handler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) {
                System.out.println("accept a socket");
                ch.config().setAllowHalfClosure(true);

                ch.pipeline().addLast(new LengthFieldPrepender(4));
                ch.pipeline().addLast(new MessageEncoder());

                ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024*500,0,4,0,4));
                ch.pipeline().addLast(new MessageDecoder());
                ch.pipeline().addLast(network);
            }
        });

        List<Peer> peers = context.getConfig().getPeers();
        for (int i = 0; i < peers.size(); i++) {
            Peer peer = peers.get(i);
            bootstrap.connect(peer.getIp(), peer.getPort());
            System.out.println("connect to "+peer.getIp()+":"+peer.getPort());
        }
    }

    public void BroadcastBlockList(){
        Message message = new Message();
        message.setType("blocks");
        network.broadcast(message);
    }

    public void BroadcastBlock(Block block){
        System.out.println("find a block");
        block.print();
        Message message = new Message();
        message.setType("block_find");
        message.setData(block);
        network.broadcast(message);
    }

}
