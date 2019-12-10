package com.ying.cloud.lycoin;

import com.bitnum.braft.core.INode;
import com.bitnum.braft.core.NodeState;
import com.bitnum.braft.core.NodeType;
import com.ying.cloud.lycoin.net.netty.ChannelSource;

public class BraftNettyNode extends ChannelSource implements INode {
    NodeState state;

    public BraftNettyNode(String host, int port) {
        super(host, port);
        state=NodeState.FOLLOWER;
    }

    @Override
    public NodeType getType() {
        return NodeType.ALL;
    }

    @Override
    public void setType(NodeType nodeType) {

    }

    @Override
    public String getName() {
        return host+":"+port;
    }

    @Override
    public void setName(String s) {

    }

    @Override
    public String getIp() {
        return host;
    }

    @Override
    public void setIp(String s) {

    }

    @Override
    public String getPort() {
        return String.valueOf(port);
    }

    @Override
    public void setPort(String s) {

    }

    @Override
    public NodeState getState() {
        return state;
    }

    @Override
    public void setState(NodeState nodeState) {
        state=nodeState;
    }

    @Override
    public boolean equals(Object obj) {

        return this.getName().equals(((BraftNettyNode)obj).getName());
    }

    @Override
    public String toString() {
        return getName()+"#"+getIp()+"#"+getPort()+"#"+getType().toString()+"#"+getState().toString();
    }
}
