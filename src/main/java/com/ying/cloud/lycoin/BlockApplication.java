package com.ying.cloud.lycoin;

import com.google.gson.Gson;
import com.ying.cloud.lycoin.config.BlockConfig;
import com.ying.cloud.lycoin.crypto.DefaultHashEncoder;
import com.ying.cloud.lycoin.crypto.HashEncoder;
import com.ying.cloud.lycoin.net.message.MessageHandler;
import com.ying.cloud.lycoin.net.message.MessageRequestBlock;
import com.ying.cloud.lycoin.models.Account;
import com.ying.cloud.lycoin.models.BlockChain;
import com.ying.cloud.lycoin.net.PeerNetworkServer;
import com.ying.cloud.lycoin.transaction.TransactionStore;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public abstract class BlockApplication implements IBlockApplication {

    private static final String DEFAULT_CONFIG_FILE  = "lycoin.conf";


    protected LycoinContext context;

    public BlockApplication(){
        context = new LycoinContext();


        try{

            List<MessageHandler>  handlers = new ArrayList<>();
            context.setHandlers(handlers);

            BlockChain chain = new BlockChain();
            context.setChain(chain);

            HashEncoder encoder =new DefaultHashEncoder();
            context.setEncoder(encoder);

            TransactionStore transactionList = new TransactionStore();
            context.setTransactions(transactionList);

            Account account = Account.init();
            context.setAccount(account);

            Gson gson = new Gson();
            String json =  FileUtils.readFileToString(new File(DEFAULT_CONFIG_FILE),Charset.forName("utf-8"));
            BlockConfig config=gson.fromJson(json,BlockConfig.class);
            context.setConfig(config);
        }
        catch (Exception error){
            System.out.println(error.getMessage());
        }

        this.init(context);
    }

    @Override
    public abstract void init(LycoinContext context);

    @Override
    public abstract void run();

    @Override
    public void setup() {

//        LycoinHttpServer server =new LycoinHttpServer(context);
//        server.run();

        PeerNetworkServer peerServer =new PeerNetworkServer(context);
        peerServer.setup();


        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try{
                        String hash = context.getChain().getLoseBlock();
                        if(hash!=null){
                            MessageRequestBlock requestBlock =new MessageRequestBlock(hash);
                            context.getNetwork().broadcast(requestBlock);
                        }
                        Thread.sleep(2000);
                    }catch (Exception err){
                        System.out.println(err.getMessage());
                    }
                }
            }
        }).start();

        this.run();
    }

    @Override
    public void handler(MessageHandler handler) {
        context.getHandlers().add(handler);
    }
}
