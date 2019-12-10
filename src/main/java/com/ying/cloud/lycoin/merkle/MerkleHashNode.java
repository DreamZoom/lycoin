package com.ying.cloud.lycoin.merkle;

import com.ying.cloud.lycoin.crypto.SHA256;
import com.ying.cloud.lycoin.transaction.Transaction;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class MerkleHashNode extends MerkleNode {

    @Override
    public String toHashString() {
        String raw = this.left.toHashString();
        if(this.right!=null){
            raw+=this.right.toHashString();
        }
        return SHA256.encode(raw);
    }

    @Override
    public void iterator(Consumer<IMerkleNode> consumer) {
        consumer.accept(left);
        if(right!=null){
            consumer.accept(right);
        }
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
