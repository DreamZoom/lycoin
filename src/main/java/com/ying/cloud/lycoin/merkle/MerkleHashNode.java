package com.ying.cloud.lycoin.merkle;

import com.ying.cloud.lycoin.crypto.SHA256;

public class MerkleHashNode extends MerkleNode {

    @Override
    public String toHashString() {
        String raw = this.left.toHashString();
        if(this.right!=null){
            raw+=this.right.toHashString();
        }
        return SHA256.encode(raw);
    }

    private MerkleNode left;

    public MerkleNode getLeft() {
        return left;
    }

    public void setLeft(MerkleNode left) {
        this.left = left;
    }

    public MerkleNode getRight() {
        return right;
    }

    public void setRight(MerkleNode right) {
        this.right = right;
    }

    private MerkleNode right;

}
