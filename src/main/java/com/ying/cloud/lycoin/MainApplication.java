package com.ying.cloud.lycoin;

import com.bitnum.braft.config.ConfigHolder;
import com.bitnum.braft.core.BraftContext;
import com.bitnum.braft.core.INode;
import com.bitnum.braft.exception.SendMessageException;
import com.bitnum.braft.net.AbstratBraftNet;
import com.ying.cloud.lycoin.config.BlockConfig;
import com.ying.cloud.lycoin.config.Peer;
import com.ying.cloud.lycoin.miner.BraftMiner;
import com.ying.cloud.lycoin.miner.IMinerEventAdapter;
import com.ying.cloud.lycoin.models.Block;
import com.ying.cloud.lycoin.net.IMessageHandler;
import com.ying.cloud.lycoin.net.Message;
import com.ying.cloud.lycoin.net.messages.MsgBlock;
import com.ying.cloud.lycoin.net.messages.MsgBraft;
import com.ying.cloud.lycoin.net.messages.MsgRequestBlock;
import com.ying.cloud.lycoin.net.messages.MsgTransaction;
import com.ying.cloud.lycoin.net.netty.ChannelSource;
import com.ying.cloud.lycoin.net.netty.NettyClientNode;
import com.ying.cloud.lycoin.net.netty.NettyServerNode;
import com.ying.cloud.lycoin.transaction.Transaction;

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



        NettyClientNode clientNode =new NettyClientNode();




        ConfigHolder holder =new ConfigHolder();
        holder.setTimeout(10000);
        holder.setRandomTimeoutLimit(5000);

        BraftNettyNode my =new BraftNettyNode(config.getIp(),config.getServerPort());
        holder.setMy(my);

        List<INode> nodes =new ArrayList<>();

        for (int i = 0; i <config.getPeers().size() ; i++) {
            BraftNettyNode nettyNode = new BraftNettyNode(config.getPeers().get(i).getIp(),config.getPeers().get(i).getServerPort());
            nodes.add(nettyNode);
            clientNode.addSource(nettyNode);
            serverNode.addSource(nettyNode);
            //clientNode.connect(config.getPeers().get(i).getIp(),config.getPeers().get(i).getServerPort());
        }

        holder.setNodes(nodes);


        AbstratBraftNet net = new AbstratBraftNet() {
            @Override
            public int sendMessageToAll(Object o) throws SendMessageException {

                System.out.println( "send--"+o.toString());


                clientNode.broadcast(new MsgBraft(o));
                return 0;
            }

            @Override
            public void sendMessageToOne(INode node, Object o) throws SendMessageException {
                System.out.println( "send--"+o.toString());
                clientNode.send((BraftNettyNode)node,new MsgBraft(o));
            }

        };

        BraftContext braftContext =new BraftContext();
        BraftMiner miner =new BraftMiner(braftContext);
        miner.setAdapter(new IMinerEventAdapter() {
            @Override
            public void onFindBlock(Block block) {
                System.out.println("block id equals "+block.getHash() +" and index equals "+block.getIndex());
                clientNode.broadcast(new MsgBlock(block,"find"));
            }

            @Override
            public void onLoseBlock(String hash) {
                System.out.println("i lose block id equals "+hash +" , request it ");
                clientNode.broadcast(new MsgRequestBlock(hash));
            }

            @Override
            public void onAcceptTransaction(Transaction transaction) {
                System.out.println("i accept a transaction id equals "+transaction.getId() +" , broadcast it ");
                clientNode.broadcast(new MsgTransaction(transaction));
            }
        });
        IMessageHandler handler =new IMessageHandler() {
            @Override
            public void handle(Object source, Message message) {
                if(message instanceof MsgBraft){
                    try{
                        System.out.println(((MsgBraft) message).getBraft());
                        net.handleMessage(((MsgBraft) message).getBraft());
                    }
                    catch (Exception error){
                        System.out.println(error.getMessage());
                    }
                }
                else {
                    miner.handle(source,message);
                    if(message instanceof MsgRequestBlock){
                        System.out.println("request a block ,id equals "+((MsgRequestBlock) message).getHash());
                        clientNode.send((ChannelSource) source,message);
                    }
                    else{
                        clientNode.broadcast(message);
                    }

                }
            }
        };

        serverNode.setHandler(handler);
        //clientNode.setHandler(handler);
        try{
            braftContext.init(holder,net);

            serverNode.setup();
            clientNode.setup();
            miner.run();

            HttpApiServer apiServer =new HttpApiServer(miner);

            apiServer.run();

        }
        catch (Exception err){

        }
    }
}
