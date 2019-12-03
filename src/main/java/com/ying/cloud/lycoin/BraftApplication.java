package com.ying.cloud.lycoin;

import com.bitnum.braft.config.ConfigHolder;
import com.bitnum.braft.core.BraftContext;
import com.bitnum.braft.core.INode;
import com.bitnum.braft.core.Node;
import com.bitnum.braft.exception.SendMessageException;
import com.bitnum.braft.net.AbstratBraftNet;
import com.bitnum.braft.net.BraftNet;
import com.ying.cloud.lycoin.message.*;
import com.ying.cloud.lycoin.models.Block;
import com.ying.cloud.lycoin.net.IPeerNetwork;
import com.ying.cloud.lycoin.transaction.AuthorizationInfo;
import com.ying.cloud.lycoin.transaction.Transaction;

import java.util.List;

public class BraftApplication  extends BlockApplication{
    @Override
    public void init(LycoinContext context) {



        ConfigHolder holder =new ConfigHolder();


        AbstratBraftNet net = new AbstratBraftNet() {
            @Override
            public int sendMessageToAll(Object o) throws SendMessageException {

                MessageBraft messageBraft =new MessageBraft(o);
                context.getNetwork().broadcast(messageBraft);
                return 0;
            }

            @Override
            public void sendMessageToOne(INode node, Object o) throws SendMessageException {

            }
        };



        try {
            BraftContext braftContext =new BraftContext();
            braftContext.init(holder,net);



            this.handler(new MessageHandler<MessageBraft>() {
                @Override
                public void handle(IPeerNetwork network, IMessageSource source, MessageBraft message) {
                   try{
                       Object msg = message.getBraft();
                       net.handleMessage(msg);
                   }
                   catch (Exception err){
                       System.out.println(err.getMessage());
                   }

                }
            });

            this.handler(new MessageHandler<MessageAuthorizationInfo>() {
                @Override
                public void handle(IPeerNetwork network, IMessageSource source, MessageAuthorizationInfo message) {
                    try{
                        AuthorizationInfo msg = message.getInfo();
                        
                    }
                    catch (Exception err){
                        System.out.println(err.getMessage());
                    }

                }
            });


        }catch (Exception err){

        }



    }

    @Override
    public void run() {

    }
}
