package com.ying.cloud.lycoin.merkle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MerkleUtils {
    public static MerkleNode tree(MerkleNode ...nodes){
        return tree(new ArrayList<MerkleNode>(Arrays.asList(nodes)));
    }

    public static MerkleNode tree(List<MerkleNode> nodes){
        int size = nodes.size()%2;
        if(size==0){
            size = nodes.size()/2;
        }
        else{
            size = nodes.size()/2 +1;
        }
        List<MerkleNode> p_nodes =new ArrayList<>();
        for (int i = 0; i < size; i+=2) {
            MerkleHashNode hashNode =new MerkleHashNode();
            hashNode.setLeft(nodes.get(i));
            if(i+1<nodes.size()){
                hashNode.setRight(nodes.get(i+1));
            }
            p_nodes.add(hashNode);
        }

        if(p_nodes.size()==1){
            return  p_nodes.get(0);
        }
        return tree(p_nodes);
    }

}
