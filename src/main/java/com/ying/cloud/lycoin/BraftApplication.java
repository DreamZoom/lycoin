package com.ying.cloud.lycoin;

import com.bitnum.braft.config.ConfigHolder;
import com.bitnum.braft.core.BraftContext;
import com.bitnum.braft.core.INode;
import com.bitnum.braft.exception.SendMessageException;
import com.bitnum.braft.net.AbstratBraftNet;
import com.google.gson.Gson;
import com.ying.cloud.lycoin.config.BlockConfig;
import com.ying.cloud.lycoin.event.Event;
import com.ying.cloud.lycoin.event.GlobalEventExecutor;
import com.ying.cloud.lycoin.event.IEventListener;
import com.ying.cloud.lycoin.miner.BraftMiner;
import com.ying.cloud.lycoin.miner.TransactionEvent;
import com.ying.cloud.lycoin.net.events.MessageEvent;
import com.ying.cloud.lycoin.net.message.*;
import com.ying.cloud.lycoin.config.Peer;
import com.ying.cloud.lycoin.net.http.HttpNetwork;
import com.ying.cloud.lycoin.net.netty.NettyNetwork;
import com.ying.cloud.lycoin.transaction.AuthorizationInfo;
import com.ying.cloud.lycoin.transaction.ITransaction;
import com.ying.cloud.lycoin.transaction.Transaction;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class BraftApplication extends BlockApplication {

    BraftContext braftContext;
    BraftMiner miner ;
    @Override
    public void init(LycoinContext context) {

        try {





            /**
             * 初始化投票网络
             */
            HttpNetwork network =new HttpNetwork(context);

            ConfigHolder holder =new ConfigHolder();
            holder.setTimeout(10000);
            holder.setRandomTimeoutLimit(5000);

            BraftNode my =new BraftNode(context.getConfig().getIp(),context.getConfig().getHttpPort());
            holder.setMy(my);

            List<Peer> list = context.getConfig().getPeers();
            List<INode> nodes =new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                BraftNode node =new BraftNode(list.get(i).getIp(),list.get(i).getHttpPort());
                nodes.add(node);
                network.addSource(node);
            }
            holder.setNodes(nodes);


            AbstratBraftNet net = new AbstratBraftNet() {
                @Override
                public int sendMessageToAll(Object o) throws SendMessageException {

                    System.out.println( "send--"+o.toString());
                    MessageBraft messageBraft =new MessageBraft(o);
                    network.broadcast(messageBraft);
                    return 0;
                }

                @Override
                public void sendMessageToOne(INode node, Object o) throws SendMessageException {
                    System.out.println("send--"+o.toString());
                    MessageBraft messageBraft =new MessageBraft(o);
                    network.sendMessage((BraftNode)node,messageBraft);
                }

            };

            braftContext =new BraftContext();
            braftContext.init(holder,net);
            /**
             * 初始化矿机
             */
            miner = new BraftMiner(braftContext);
            context.setMiner(miner);

            /**
             * 初始化通信网络
             */
            NettyNetwork nettyNetwork =new NettyNetwork(context);
            context.addNetwork(nettyNetwork);
            context.addNetwork(network);


            GlobalEventExecutor.INSTANCE.addEventListener(new IEventListener<Event<MessageBraft>>() {
                @Override
                public void handle(Event<MessageBraft> event) {

                    MessageBraft messageBlock = event.getData();

                    try {
                        System.out.println(messageBlock.getBraft().toString());
                        net.handleMessage(messageBlock.getBraft());
                    }
                    catch (Exception err){

                    }
                }
            });



            GlobalEventExecutor.INSTANCE.addEventListener(new IEventListener<Event<MessageBlock>>() {
                @Override
                public void handle(Event<MessageBlock> event) {

                    MessageBlock messageBlock = event.getData();
                    if(messageBlock.getTag().equals("find")){
                        messageBlock.setTag("broadcast");
                        nettyNetwork.broadcast(messageBlock);
                    }

                }
            });

            GlobalEventExecutor.INSTANCE.addEventListener(new IEventListener<Event<AuthorizationInfo>>() {
                @Override
                public void handle(Event<AuthorizationInfo> event) {
                    if(event.getSource()==null){
                        GlobalEventExecutor.INSTANCE.dispatch(new Event<>(event.getSource(),new MessageTransaction(event.getData())));
                        nettyNetwork.broadcast(new MessageTransaction(event.getData()));
                    }
                }
            });

//            GlobalEventExecutor.INSTANCE.addEventListener(new IEventListener<Event<MessageTransaction>>() {
//                @Override
//                public void handle(Event<MessageTransaction> event) {
//                    System.out.println("receive a transaction id"+event.getData().getTransaction().getId());
//                    nettyNetwork.broadcast(event.getData());
//                }
//            });

            GlobalEventExecutor.INSTANCE.addEventListener(new IEventListener<Event<MessageRequestBlock>>() {
                @Override
                public void handle(Event<MessageRequestBlock> event) {
                    //GlobalEventExecutor.INSTANCE.dispatch(event);
                    if(event.getSource()==null){
                        System.out.println("request a block id="+event.getData().getHash());
                        nettyNetwork.broadcast(event.getData());
                    }

                }
            });



            HttpApiServer httpApiServer =new HttpApiServer(miner);
            httpApiServer.run();

        }catch (Exception err){

        }



    }


    public void run() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    miner.run();
                }
                catch (Exception err){
                    System.out.println(err.getMessage());
                }
            }
        }).start();

    }
}
