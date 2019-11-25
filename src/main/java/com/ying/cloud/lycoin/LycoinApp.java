//package com.ying.cloud.lycoin;
//
//import com.google.gson.Gson;
//import com.ying.cloud.lycoin.models.Block;
//import com.ying.cloud.lycoin.models.BlockChain;
//import com.ying.cloud.lycoin.models.PeerConfig;
//import com.ying.cloud.lycoin.net.PeerNetworkServer;
//
//import org.apache.commons.io.FileUtils;
//
//import java.io.File;
//import java.io.IOException;
//import java.nio.charset.Charset;
//
//public class LycoinApp {
//
//    private final  BlockChain chain;
//
//    public  LycoinApp(){
//        chain =new BlockChain();
//    }
//    public void run(){
//        try {
//            Gson gson = new Gson();
//            String json =  FileUtils.readFileToString(new File("peer.conf"),Charset.forName("utf-8"));
//            PeerConfig config=gson.fromJson(json,PeerConfig.class);
//
//            PeerNetworkServer network =new PeerNetworkServer(config,chain);
//            network.setup();
//
//
//
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    while (true){
//                        network.BroadcastBlockList();
//                        try{
//                            Thread.sleep(1000);
//                        }catch (Exception err){}
//                    }
//                }
//            }).start();
//            chain.findNextBlock((block)->{
//                Block last = chain.getLast();
//                if(BlockChain.validBlock(last,block)){
//                    chain.addBlock(block);
//                    network.BroadcastBlock(block);
//                }
//                return  true;
//            });
//
//
//        }
//        catch (IOException err){
//            System.out.println(err.getMessage());
//        }catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}
