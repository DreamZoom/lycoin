package com.ying.cloud.lycoin;

import com.google.gson.Gson;
import com.ying.cloud.lycoin.crypto.DefaultHashEncoder;
import com.ying.cloud.lycoin.crypto.HashEncoder;
import com.ying.cloud.lycoin.message.MessageBlock;
import com.ying.cloud.lycoin.message.MessageHandler;
import com.ying.cloud.lycoin.models.Block;
import com.ying.cloud.lycoin.models.BlockChain;
import com.ying.cloud.lycoin.net.LycoinHttpServer;
import com.ying.cloud.lycoin.net.PeerNetworkServer;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public abstract class BlockApplication implements IBlockApplication {

    private static final String DEFAULT_CONFIG_FILE  = "lycoin.conf";


    private LycoinContext context;

    public BlockApplication(){
        context = new LycoinContext();

        List<MessageHandler>  handlers = new ArrayList<>();
        context.setHandlers(handlers);

        BlockChain chain = new BlockChain();
        context.setChain(chain);

        HashEncoder encoder =new DefaultHashEncoder();
        context.setEncoder(encoder);

        try{
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
    public void setup() {

        LycoinHttpServer server =new LycoinHttpServer(context);
        server.run();

        PeerNetworkServer peerServer =new PeerNetworkServer(context);
        peerServer.setup();

        context.getChain().findNextBlock((block)->{
            MessageBlock messageBlock =new MessageBlock("find");
            messageBlock.setBlock(block);
            context.getNetwork().trigger(messageBlock);
            return  true;
        });
    }

    @Override
    public void handler(MessageHandler handler) {
        context.getHandlers().add(handler);
    }
}
