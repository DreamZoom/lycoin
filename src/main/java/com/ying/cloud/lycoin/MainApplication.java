package com.ying.cloud.lycoin;

import com.bitnum.braft.config.ConfigHolder;
import com.bitnum.braft.core.BraftContext;
import com.bitnum.braft.core.INode;
import com.bitnum.braft.exception.SendMessageException;
import com.bitnum.braft.net.AbstratBraftNet;
import com.ying.cloud.lycoin.config.BlockConfig;
import com.ying.cloud.lycoin.config.Peer;
import com.ying.cloud.lycoin.net.IMessageHandler;
import com.ying.cloud.lycoin.net.Message;
import com.ying.cloud.lycoin.net.netty.NettyClientNode;
import com.ying.cloud.lycoin.net.netty.NettyServerNode;

import java.util.ArrayList;
import java.util.List;

public class MainApplication {
    public static void main(String[] args){
//          LycoinApplication application =new LycoinApplication();
//          application.setup();

        BlockConfig config = BlockConfig.load();

//        BlockConfig config1 =new BlockConfig();
//        config1.setServerPort(8887);
//        List<Peer> peers1=new ArrayList<>();
//        Peer peer =new Peer();
//        peer.setServerPort(8888);
//        peers1.add(peer);
//        peer =new Peer();
//        peer.setServerPort(8889);
//        peers1.add(peer);
//        config1.setPeers(peers1);
//
//        BlockConfig config2 =new BlockConfig();
//        config2.setServerPort(8888);
//        List<Peer> peers2=new ArrayList<>();
//        Peer peer2 =new Peer();
//        peer2.setServerPort(8887);
//        peers2.add(peer2);
//        peer2 =new Peer();
//        peer2.setServerPort(8889);
//        peers2.add(peer2);
//        config2.setPeers(peers2);
//
//        BlockConfig config3 =new BlockConfig();
//        config3.setServerPort(8889);
//        List<Peer> peers3=new ArrayList<>();
//        Peer peer3 =new Peer();
//        peer3.setServerPort(8888);
//        peers3.add(peer3);
//        peer3 =new Peer();
//        peer3.setServerPort(8887);
//        peers3.add(peer3);
//        config3.setPeers(peers3);

        NettyServerNode serverNode =new NettyServerNode(config.getServerPort());
        serverNode.setup();


        NettyClientNode clientNode =new NettyClientNode();
        clientNode.setup();



        ConfigHolder holder =new ConfigHolder();
        holder.setTimeout(10000);
        holder.setRandomTimeoutLimit(5000);

        BraftNettyNode my =new BraftNettyNode(config.getIp(),config.getServerPort());
        holder.setMy(my);

        List<INode> nodes =new ArrayList<>();

        for (int i = 0; i <config.getPeers().size() ; i++) {
            nodes.add(new BraftNettyNode(config.getPeers().get(i).getIp(),config.getPeers().get(i).getServerPort()));
            clientNode.connect(config.getPeers().get(i).getIp(),config.getPeers().get(i).getServerPort());
        }

        holder.setNodes(nodes);


        AbstratBraftNet net = new AbstratBraftNet() {
            @Override
            public int sendMessageToAll(Object o) throws SendMessageException {

                System.out.println( "send--"+o.toString());


                clientNode.broadcast(new Message<>("braft",o));
                return 0;
            }

            @Override
            public void sendMessageToOne(INode node, Object o) throws SendMessageException {
                System.out.println( "send--"+o.toString());
                clientNode.send((BraftNettyNode)node,new Message<>("braft",o));
            }

        };

        BraftContext braftContext =new BraftContext();
        IMessageHandler handler =new IMessageHandler() {
            @Override
            public void handle(Object source, Message message) {
                if(message.getType().equals("braft")){
                    try{
                        System.out.println(message.getData());
                        net.handleMessage(message.getData());

                    }
                    catch (Exception error){
                        error.printStackTrace();

                        System.out.println(error.getMessage());
                    }

                }
                else {
                    //miner.handle(source,message);
                }
            }
        };

        serverNode.setHandler(handler);

        try{
            braftContext.init(holder,net);
        }
        catch (Exception err){

        }
    }
}
