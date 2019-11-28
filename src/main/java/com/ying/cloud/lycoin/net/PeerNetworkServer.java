package com.ying.cloud.lycoin.net;

import com.google.gson.Gson;
import com.ying.cloud.lycoin.LycoinContext;
import com.ying.cloud.lycoin.message.Message;
import com.ying.cloud.lycoin.message.MessageBlock;
import com.ying.cloud.lycoin.message.MessageChain;
import com.ying.cloud.lycoin.message.MessageHandler;
import com.ying.cloud.lycoin.models.Block;
import com.ying.cloud.lycoin.models.BlockChain;
import com.ying.cloud.lycoin.models.Peer;
import com.ying.cloud.lycoin.utils.HttpUtils;
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

    LycoinContext context;
    PeerNetwork network ;

    public PeerNetworkServer(LycoinContext context){
        this.context = context;
        network = new PeerNetwork();
        context.setNetwork(network);

        context.getHandlers().forEach((handler)->{
            network.handler(handler);
        });
    }


    public void setup(){
        try{
            setupServer();
            setupClients();
            setupHttpRequest();
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

    public void setupHttpRequest(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try{
                        List<Peer> peerList =  context.getConfig().getPeers();

                        for (Peer peer:peerList) {
                            try{
                                String chainString = HttpUtils.doGet("http://"+peer.getIp()+":"+peer.getHttpPort()+"?action=blocks");
                                BlockChain chain =new Gson().fromJson(chainString,BlockChain.class);
                                MessageChain message =new MessageChain();
                                message.setChain(chain);
                                network.trigger(message);

                            }catch (Exception error){
                                //System.out.println(error.getMessage());
                            }
                        }
                        Thread.sleep(2000);
                    }catch (Exception err){
                        System.out.println(err.getMessage());
                    }
                }
            }
        }).start();
    }


}
