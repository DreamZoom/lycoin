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
import com.ying.cloud.lycoin.net.ISourceAdapter;
import com.ying.cloud.lycoin.net.Message;
import com.ying.cloud.lycoin.net.Source;
import com.ying.cloud.lycoin.net.messages.*;
import com.ying.cloud.lycoin.net.netty.ChannelSource;
import com.ying.cloud.lycoin.net.netty.NettyClientNode;
import com.ying.cloud.lycoin.net.netty.NettyServerNode;
import com.ying.cloud.lycoin.transaction.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainApplication {
    public static void main(String[] args){

        BlockConfig config = BlockConfig.load();
        NettyServerNode serverNode =new NettyServerNode(config.getIp(),config.getServerPort());
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
                       // clientNode.send((ChannelSource) source,message);
                    }
                    else{
                        clientNode.broadcast(message);
                    }

                }
            }
        };

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
                ChannelSource s = serverNode.getSource(source.id());
                serverNode.removeSource(s);
                clientNode.removeSource(s);
            }
        });
        serverNode.setHandler(handler);
        //clientNode.setHandler(handler);



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
