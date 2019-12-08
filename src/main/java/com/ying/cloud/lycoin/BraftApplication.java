package com.ying.cloud.lycoin;

import com.bitnum.braft.config.ConfigHolder;
import com.bitnum.braft.core.BraftContext;
import com.bitnum.braft.core.INode;
import com.bitnum.braft.exception.SendMessageException;
import com.bitnum.braft.net.AbstratBraftNet;
import com.ying.cloud.lycoin.miner.BraftMiner;

import com.ying.cloud.lycoin.config.Peer;
import com.ying.cloud.lycoin.net.Message;
import com.ying.cloud.lycoin.net.http.HttpNetwork;


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

                    network.broadcast(new Message<>("braft",o));
                    return 0;
                }

                @Override
                public void sendMessageToOne(INode node, Object o) throws SendMessageException {
                    System.out.println("send--"+o.toString());
                    network.send((BraftNode)node,new Message<>("braft",o));
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
//            NettyNetwork nettyNetwork =new NettyNetwork(context);
//            context.addNetwork(nettyNetwork);
//            context.addNetwork(network);
//
//
//            miner.setAdapter(new IMinerEventAdapter() {
//                @Override
//                public void onFindBlock(Block block) {
//                    MessageBlock messageBlock=new MessageBlock("broadcast",block);
//                    nettyNetwork.broadcast(messageBlock);
//                }
//
//                @Override
//                public void onLoseBlock(String hash) {
//                    MessageRequestBlock messageRequestBlock =new MessageRequestBlock(hash);
//                    nettyNetwork.broadcast(messageRequestBlock);
//                }
//            });
//
//
//            GlobalEventExecutor.INSTANCE.addEventListener(new IEventListener<Event<MessageBraft>>() {
//                @Override
//                public void handle(Event<MessageBraft> event) {
//
//                    MessageBraft messageBlock = event.getData();
//
//                    try {
//                        System.out.println(messageBlock.getBraft().toString());
//                        net.handleMessage(messageBlock.getBraft());
//                    }
//                    catch (Exception err){
//
//                    }
//                }
//            });
//
//
//            GlobalEventExecutor.INSTANCE.addEventListener(new IEventListener<Event<AuthorizationInfo>>() {
//                @Override
//                public void handle(Event<AuthorizationInfo> event) {
//                    if(event.getSource()==null){
//                        GlobalEventExecutor.INSTANCE.dispatch(new Event<>(event.getSource(),new MessageTransaction(event.getData())));
//                        nettyNetwork.broadcast(new MessageTransaction(event.getData()));
//                    }
//                }
//            });





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
