package com.ying.cloud.lycoin;

import com.google.gson.Gson;
import com.ying.cloud.lycoin.crypto.DefaultHashEncoder;
import com.ying.cloud.lycoin.crypto.HashEncoder;
import com.ying.cloud.lycoin.event.LycoinEventManager;
import com.ying.cloud.lycoin.models.*;
import com.ying.cloud.lycoin.net.LycoinHttpServer;
import com.ying.cloud.lycoin.net.PeerNetworkServer;
import com.ying.cloud.lycoin.utils.HttpUtils;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Lycoin 币应用容器
 */
public class LycoinApplication {

    /**
     * context 上下文
     */
    private LycoinApplicationContext context;

    private static final String DEFAULT_CONFING_FILE  = "lycoin.conf";

    public  LycoinApplication(){
        context = new LycoinApplicationContext();
    }


    public  void initApplication(LycoinApplicationContext context){
        BlockChain chain = new BlockChain();
        context.setChain(chain);
    }

    /**
     * 加载配置文件
     * @param context
     * @throws Exception
     */
    public void loadConfig(LycoinApplicationContext context) throws Exception{

        HashEncoder encoder =new DefaultHashEncoder();
        context.setEncoder(encoder);

        Gson gson = new Gson();
        String json =  FileUtils.readFileToString(new File(DEFAULT_CONFING_FILE),Charset.forName("utf-8"));
        LycoinConfig config=gson.fromJson(json,LycoinConfig.class);
        context.setConfig(config);
    }



    public void setupServer(LycoinApplicationContext context){
        PeerNetworkServer peerServer =new PeerNetworkServer(context);
        peerServer.setup();
    }

    public void setupRpcServer(LycoinApplicationContext context){

    }

    public void setupHttpServer(LycoinApplicationContext context){
        LycoinHttpServer server =new LycoinHttpServer(context);
        server.run();
    }

    public void  initEventSystem(LycoinApplicationContext context){
        LycoinEventManager eventManager =new LycoinEventManager();
        context.setEventManager(eventManager);
    }


    public void run(){
        try {
            initApplication(context);

            loadConfig(context);

            initEventSystem(context);

            setupServer(context);

            setupRpcServer(context);

            setupHttpServer(context);


            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true){
                        try{
                            List<Peer> peerList =  context.getConfig().getPeers();

                            for (Peer peer:peerList) {
                                try{
                                    String chainString = HttpUtils.doGet("http://"+peer.getIp()+":"+peer.getHttpPort()+"?action=blocks");
                                    BlockChain chain =new Gson().fromJson(chainString,BlockChain.class);
                                    if(chain.valid()){
                                        if(context.replace(chain)){
                                            System.out.println("accept a chain");
                                            context.fireEvent("replace");
                                        }
                                    }

                                }catch (Exception error){
                                    //System.out.println(error.getMessage());
                                }

                            }
                            Thread.sleep(2000);
                        }catch (Exception err){
                            System.out.println(err.getMessage());
                        }
                    }
                }
            }).start();

            context.getChain().findNextBlock((block)->{
                Block last = context.getChain().getLast();
                if(BlockChain.validBlock(last,block)){
                    context.getChain().addBlock(block);
                    MessageFindBlock message =new MessageFindBlock();
                    message.setBlock(block);
                    context.getNetwork().broadcast(message);
                }
                return  true;
            });


        }catch (Exception error){
            System.out.println(error.getMessage());
        }

    }

}
