package com.ying.cloud.lycoin.net;

import com.ying.cloud.lycoin.models.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

import java.util.List;

public class PeerNetwork {

    PeerConfig config;

    private PeerManager peerManager;
    public PeerNetwork(PeerConfig config, BlockChain chain){
        peerManager = new PeerManager(chain);
        this.config = config;
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


                        //ch.pipeline().addLast(new StringEncoder());
                        ch.pipeline().addLast(new LengthFieldPrepender(4));
                        ch.pipeline().addLast(new MessageEncoder());

                        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024*500,0,4,0,4));
                        //ch.pipeline().addLast(new StringDecoder());
                        ch.pipeline().addLast(new MessageDecoder());



                        //LengthFieldBasedFrameDecoder

                        //ch.pipeline().addLast(new MessageEncoder());
                        //ch.pipeline().addLast(new MessageDecoder());
                        //ch.pipeline().addLast( new LengthFieldPrepender(4));
                        //ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024*500,0,4,0,4));
                        ch.pipeline().addLast(peerManager);
                    }
                });



        serverBootstrap.bind(config.getPort());

        System.out.println("server setup at "+config.getPort());
    }

    public void connectNodes(){

        Bootstrap bootstrap = new Bootstrap();
        NioEventLoopGroup group = new NioEventLoopGroup();
        bootstrap.group(group)
                .channel(NioSocketChannel.class).handler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) {
                System.out.println("accept a socket");
                ch.config().setAllowHalfClosure(true);

                //ch.pipeline().addLast(new StringEncoder());
                ch.pipeline().addLast(new LengthFieldPrepender(4));
                ch.pipeline().addLast(new MessageEncoder());

                ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024*500,0,4,0,4));
                //ch.pipeline().addLast(new StringDecoder());
                ch.pipeline().addLast(new MessageDecoder());


                //ch.pipeline().addLast(new MessageEncoder());
                //ch.pipeline().addLast(new MessageDecoder());
                //ch.pipeline().addLast( new LengthFieldPrepender(4));
                //ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024*500,0,4,0,4));
                ch.pipeline().addLast(peerManager);
            }
        });


        List<Peer> peers = config.getPeers();
        for (int i = 0; i < peers.size(); i++) {
            Peer peer = peers.get(i);
            bootstrap.connect(peer.getIp(), peer.getPort());
            System.out.println("connect to "+peer.getIp()+":"+peer.getPort());
        }
    }

    public void BroadcastBlockList(){
        Message message = new Message();
        message.setType("blocks");
        peerManager.broadcast(message);
    }

    public void BroadcastBlock(Block block){
        System.out.println("find a block");
        block.print();
        Message message = new Message();
        message.setType("block_find");
        message.setData(block);
        peerManager.broadcast(message);
    }

}
