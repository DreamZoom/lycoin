package com.ying.cloud.lycoin;

import com.bitnum.braft.config.ConfigHolder;
import com.bitnum.braft.core.BraftContext;
import com.bitnum.braft.core.INode;
import com.bitnum.braft.exception.SendMessageException;
import com.bitnum.braft.net.AbstratBraftNet;
import com.ying.cloud.lycoin.net.message.*;
import com.ying.cloud.lycoin.models.Block;
import com.ying.cloud.lycoin.config.Peer;
import com.ying.cloud.lycoin.net.IMessageHandler;
import com.ying.cloud.lycoin.net.IPeerNetwork;
import com.ying.cloud.lycoin.net.ISource;
import com.ying.cloud.lycoin.net.http.HttpNetwork;
import com.ying.cloud.lycoin.transaction.ITransaction;

import java.util.ArrayList;
import java.util.List;

public class BraftApplication  extends BlockApplication{

    BraftContext braftContext;
    @Override
    public void init(LycoinContext context) {


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




        try {
            braftContext =new BraftContext();
            braftContext.init(holder,net);


            network.handler(new IMessageHandler<MessageBraft>() {
                @Override
                public void handle(ISource source, MessageBraft messageBraft) {
                    Object o = messageBraft.getBraft();
                    try{
                        System.out.println("recive--"+o.toString());
                        net.handleMessage(o);

                    }catch (Exception err){
                        System.out.println(err.getMessage());
                    }
                }

            });




            network.setup();

        }catch (Exception err){

        }

        this.handler(new MessageHandler<MessageBlock>() {
            @Override
            public void handle(IPeerNetwork network, IMessageSource source, MessageBlock message) {
                Block block = message.getBlock();
                if(context.getChain().accept(block)){
                    System.out.println(message.toString());
                    message.setTag("broadcast");
                    network.broadcast(message);
                }

            }
        });

        /**
         * 请求块消息处理
         */
        this.handler(new MessageHandler<MessageRequestBlock>() {
            @Override
            public void handle(IPeerNetwork network,IMessageSource source, MessageRequestBlock message) {
                System.out.println(message.toString());
                String hash = message.getHash();
                Block block = context.getChain().findBlock(hash);
                if(block!=null){
                    MessageBlock messageBlock =new MessageBlock("broadcast",block);
                    network.broadcast(messageBlock);
                }
            }
        });

        /**
         * 交易消息处理
         *
         */
        this.handler(new MessageHandler<MessageTransaction>() {
            @Override
            public void handle(IPeerNetwork network, IMessageSource source, MessageTransaction message) {
                ITransaction transaction = message.getTransaction();
                if(context.getTransactions().addTransaction(transaction)) {
                    network.broadcast(message);
                }
            }
        });




    }

    @Override
    public void run() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try{
                        Thread.sleep(3000);
                        if(braftContext.getLeader().equals(braftContext.getMy()) && context.getTransactions().getTransactions().size()>0){
                            Block block = context.getChain().getNextBlock(context);
                            MessageBlock messageBlock =new MessageBlock("find",block);
                            context.getNetwork().trigger(messageBlock,null);
                        }

                    }
                    catch (Exception err){
                        System.out.println(err.getMessage());
                    }


                }
            }
        }).start();

    }
}
