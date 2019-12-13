package com.ying.cloud.lycoin.net.messages;

import com.ying.cloud.lycoin.config.Peer;
import com.ying.cloud.lycoin.net.Message;
import com.ying.cloud.lycoin.net.Source;

import java.util.List;

public class MsgPeers extends Message {


    public List<Peer> getPeers() {
        return peers;
    }

    public void setPeers(List<Peer> peers) {
        this.peers = peers;
    }

    public MsgPeers(List<Peer> peers) {
        this.peers = peers;
    }

    private List<Peer> peers;
}
