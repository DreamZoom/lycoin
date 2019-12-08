package com.ying.cloud.lycoin;

import com.ying.cloud.lycoin.config.BlockConfig;
import com.ying.cloud.lycoin.config.Peer;

import java.util.ArrayList;
import java.util.List;

public class MainApplication {
    public static void main(String[] args){
//          LycoinApplication application =new LycoinApplication();
//          application.setup();

        BlockConfig config1 =new BlockConfig();
        config1.setServerPort(8887);
        List<Peer> peers1=new ArrayList<>();
        Peer peer =new Peer();
        peer.setServerPort(8888);
        peers1.add(peer);
        peer =new Peer();
        peer.setServerPort(8889);
        peers1.add(peer);
        config1.setPeers(peers1);

        BlockConfig config2 =new BlockConfig();
        config2.setServerPort(8888);
        List<Peer> peers2=new ArrayList<>();
        Peer peer2 =new Peer();
        peer2.setServerPort(8887);
        peers2.add(peer2);
        peer2 =new Peer();
        peer2.setServerPort(8889);
        peers2.add(peer2);
        config2.setPeers(peers2);

        BlockConfig config3 =new BlockConfig();
        config3.setServerPort(8889);
        List<Peer> peers3=new ArrayList<>();
        Peer peer3 =new Peer();
        peer3.setServerPort(8888);
        peers3.add(peer3);
        peer3 =new Peer();
        peer3.setServerPort(8887);
        peers3.add(peer3);
        config3.setPeers(peers3);


        TestServer server=new TestServer(config1);
        server.setShowMessage(true);
        server.run();

        new TestServer(config2).run();

        new TestServer(config3).run();
    }
}
