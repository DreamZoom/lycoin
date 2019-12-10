package com.ying.cloud.lycoin;

import com.bitnum.braft.config.ConfigHolder;
import com.bitnum.braft.core.BraftContext;
import com.bitnum.braft.core.INode;
import com.bitnum.braft.exception.SendMessageException;
import com.bitnum.braft.net.AbstratBraftNet;
import com.ying.cloud.lycoin.config.BlockConfig;
import com.ying.cloud.lycoin.miner.BraftMiner;
import com.ying.cloud.lycoin.miner.IMinerEventAdapter;
import com.ying.cloud.lycoin.models.Block;
import com.ying.cloud.lycoin.net.Message;
import com.ying.cloud.lycoin.net.IMessageHandler;
import com.ying.cloud.lycoin.net.messages.MsgBlock;
import com.ying.cloud.lycoin.net.messages.MsgBraft;
import com.ying.cloud.lycoin.net.messages.MsgRequestBlock;
import com.ying.cloud.lycoin.net.netty.NettyClientNode;
import com.ying.cloud.lycoin.net.netty.NettyServerNode;
import com.ying.cloud.lycoin.transaction.Transaction;

import java.util.ArrayList;
import java.util.List;

public class TestServer {
    public TestServer(BlockConfig config) {
        this.config = config;
    }

    BlockConfig config;

    public void setShowMessage(boolean showMessage) {
        this.showMessage = showMessage;
    }

    boolean showMessage =false;


    public void run(){
        try{


            NettyServerNode serverNode =new NettyServerNode(config.getServerPort());
            serverNode.setup();


            NettyClientNode clientNode =new NettyClientNode();
            clientNode.setup();


            ConfigHolder holder =new ConfigHolder();
            holder.setTimeout(10000);
            holder.setRandomTimeoutLimit(5000);

            BraftNettyNode my =new BraftNettyNode("127.0.0.1",config.getServerPort());
            holder.setMy(my);

            List<INode> nodes =new ArrayList<>();

            for (int i = 0; i <config.getPeers().size() ; i++) {
                nodes.add(new BraftNettyNode("127.0.0.1",config.getPeers().get(i).getServerPort()));
                //clientNode.connect("127.0.0.1",config.getPeers().get(i).getServerPort());
            }

            holder.setNodes(nodes);


            AbstratBraftNet net = new AbstratBraftNet() {
                @Override
                public int sendMessageToAll(Object o) throws SendMessageException {

                    if(showMessage){
                        System.out.println( "send--"+o.toString());
                    }


                    clientNode.broadcast(new MsgBraft(o));
                    return 0;
                }

                @Override
                public void sendMessageToOne(INode node, Object o) throws SendMessageException {
                    if(showMessage){
                        System.out.println( "send--"+o.toString());
                    }
                    clientNode.send((BraftNettyNode)node,new MsgBraft(o));
                }

            };

            BraftContext braftContext =new BraftContext();
            braftContext.init(holder,net);

            BraftMiner miner =new BraftMiner(braftContext);
            miner.addEventListener(Block.class,(source,block)->{

            });

            IMessageHandler handler =new IMessageHandler() {
                @Override
                public void handle(Object source, Message message) {
                    if(message instanceof MsgBraft){
                        try{
                            if(showMessage){
                                System.out.println(((MsgBraft) message).getBraft());
                            }
                            net.handleMessage(((MsgBraft) message).getBraft());

                        }
                        catch (Exception error){
                            error.printStackTrace();

                            System.out.println(error.getMessage());
                        }

                    }
                    else {
                        miner.handle(source,message);
                    }
                }
            };

            serverNode.setHandler(handler);


            miner.setAdapter(new IMinerEventAdapter(){
                @Override
                public void onFindBlock(Block block) {
                    clientNode.broadcast(new MsgBlock(block,"find"));
                }

                @Override
                public void onLoseBlock(String hash) {
                    clientNode.broadcast(new MsgRequestBlock(hash));
                }

                @Override
                public void onAcceptTransaction(Transaction transaction) {

                }
            });

            miner.run();

        }
        catch (Exception err){

        }
    }
}
