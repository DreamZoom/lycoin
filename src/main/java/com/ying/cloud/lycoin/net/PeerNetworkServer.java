package com.ying.cloud.lycoin.net;

import com.google.gson.Gson;
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
import java.util.List;

public class PeerNetworkServer {

    LycoinApplicationContext context;

    private PeerNetwork network;
    public PeerNetworkServer(LycoinApplicationContext context){
        network = new PeerNetwork(context);
        this.context = context;
        network.hander(new LycoinMessageHandler<MessageFindBlock>() {
            @Override
            public void handle(ChannelHandlerContext ctx, PeerNetwork network, MessageFindBlock message) {
                Block block = message.getBlock();
                Block last = context.getChain().getLast();
                if(BlockChain.validBlock(last,block)){
                    context.getChain().addBlock(block);
                    System.out.println("accept a block");
                    block.print();
                    network.broadcast(message);
                }
            }
        });
        context.setNetwork(network);
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
            ConnectionListener listener = new ConnectionListener(bootstrap,peer.getIp(),peer.getServerPort());
            bootstrap.connect(peer.getIp(), peer.getServerPort()).addListener(listener);
        }
    }
}
