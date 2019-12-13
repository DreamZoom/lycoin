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
import com.ying.cloud.lycoin.net.*;
import com.ying.cloud.lycoin.net.messages.*;
import com.ying.cloud.lycoin.net.netty.ChannelSource;
import com.ying.cloud.lycoin.net.netty.NettyClientNode;
import com.ying.cloud.lycoin.net.netty.NettyServerNode;
import com.ying.cloud.lycoin.transaction.Transaction;

import java.util.ArrayList;
import java.util.List;

public class MainApplication {
    public static void main(String[] args){

        BlockConfig config = BlockConfig.load();
        NettyServerNode<BraftNettyNode> serverNode =new NettyServerNode<>(config.getIp(),config.getServerPort());
        NettyClientNode<BraftNettyNode> clientNode =new NettyClientNode<>();

        ConfigHolder holder =new ConfigHolder();
        holder.setTimeout(5000);
        holder.setRandomTimeoutLimit(3000);

        BraftNettyNode my =new BraftNettyNode(config.getIp(),config.getServerPort());
        holder.setMy(my);

        List<INode> nodes =new ArrayList<>();

        for (int i = 0; i <config.getPeers().size() ; i++) {
            BraftNettyNode nettyNode = new BraftNettyNode(config.getPeers().get(i).getIp(),config.getPeers().get(i).getServerPort());
            nodes.add(nettyNode);
            clientNode.addSource(nettyNode);
            serverNode.addSource(nettyNode);
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
            public void onRequestLastBlock() {
                System.out.println("i request last block");
                clientNode.broadcast(new MsgRequestLastBlock());
            }

            @Override
            public void onAcceptTransaction(Transaction transaction) {
                System.out.println("i accept a transaction id equals "+transaction.getId() +" , broadcast it ");
                clientNode.broadcast(new MsgTransaction(transaction));
            }
        });

        serverNode.setSourceAdapter(new ISourceAdapter<ChannelSource>() {
            @Override
            public void onAdded(ChannelSource source) {

                BraftNettyNode nettyNode = new BraftNettyNode(source.host,source.port);
                nettyNode.setReceiver(source.getReceiver());
                System.out.println(source.host+":"+source.port);
                braftContext.addNode(nettyNode);
                serverNode.addSource(nettyNode);
                clientNode.connectSource(nettyNode);
            }

            @Override
            public void onRemoved(ChannelSource source) {

                BraftNettyNode s = serverNode.getSource(source.id());

                serverNode.removeSource(s);
                clientNode.removeSource(s);
            }
        });
        serverNode.setHandler(new IMessageHandler<BraftNettyNode>() {
            @Override
            public void handle(BraftNettyNode source, Message message) {
                if(message instanceof MsgBraft){
                    try{
                        System.out.println(((MsgBraft) message).getBraft());
                        net.handleMessage(((MsgBraft) message).getBraft());
                    }
                    catch (Exception error){
                        System.out.println(error.getMessage());
                    }
                }
                else if(message instanceof MsgRequestPeers){
                    System.out.println("response peers");
                    List<Peer> list =new ArrayList<>();
                    clientNode.getSources().forEach((s)->{
                        list.add(new Peer(s.host,s.port));
                    });
                    Message msg =new MsgPeers(list);

                    clientNode.send(source,msg);
                }
                else if(message instanceof MsgPeers){
                    List<Peer> peers = ((MsgPeers) message).getPeers();
                    for (int i = 0; i <peers.size() ; i++) {
                        Peer peer =peers.get(i);
                        if(peer.getIp().equals(config.getIp())){
                            continue;
                        }
                        clientNode.connectSource(new BraftNettyNode(peer.getIp(),peer.getServerPort()));
                    }
                }
                else {
                    miner.handle(source,message);
                    if(message instanceof MsgRequestBlock){
                        System.out.println("request a block ,id equals "+((MsgRequestBlock) message).getHash());
                        // clientNode.send((ChannelSource) source,message);
                    }
                    else{
                        clientNode.broadcast(message);
                    }

                }
            }
        });
        clientNode.setConnectAdapter(new IConnectAdapter<BraftNettyNode>() {
            @Override
            public void onActive(BraftNettyNode source) {
                if(source!=null){
                    System.out.println("request peers");
                    clientNode.send(source,new MsgRequestPeers());
                }
            }

            @Override
            public void onInactive(BraftNettyNode source) {

            }
        });



        try{

            braftContext.init(holder,net);

            serverNode.setup();
            clientNode.setup();
            miner.run();

            HttpApiServer apiServer =new HttpApiServer(miner);
            apiServer.setMy(my);
            apiServer.setClientNode(clientNode);

            apiServer.run();

        }
        catch (Exception err){

        }
    }
}
