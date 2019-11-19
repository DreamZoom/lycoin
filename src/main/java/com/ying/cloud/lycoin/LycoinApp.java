package com.ying.cloud.lycoin;

import com.google.gson.Gson;
import com.ying.cloud.lycoin.domain.BlockChain;
import com.ying.cloud.lycoin.domain.Peer;
import com.ying.cloud.lycoin.domain.PeerConfig;
import com.ying.cloud.lycoin.net.PeerNetwork;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

public class LycoinApp {

    private BlockChain chain;
    public  LycoinApp(){
        chain =new BlockChain();
        for (int i = 0; i < 100; i++) {
            chain.findNextBlock();
        }

        boolean v = BlockChain.validNewChain(chain.getChain());
        chain.print();

        try {
            Gson gson = new Gson();
            String json =  FileUtils.readFileToString(new File("peer.conf"),Charset.forName("utf-8"));
            PeerConfig config=gson.fromJson(json,PeerConfig.class);

            PeerNetwork network =new PeerNetwork(config);
            network.setupServer();
            network.connectNodes();

            while (true){
                network.broadcast();
                Thread.sleep(2000);
            }

        }
        catch (IOException err){
            System.out.println(err.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
}
