package com.ying.cloud.lycoin.merkle;

import com.ying.cloud.lycoin.transaction.Transaction;

import java.io.Serializable;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class MerkleNode implements IMerkleNode,Serializable {

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    private String hash;
    @Override
    public String toHashString() {
        return null;
    }

    @Override
    public boolean verify() {
        return this.hash.equals(this.toHashString());
    }

    @Override
    public void encode() {
        this.hash = this.toHashString();
    }

    @Override
    public void iterator(Consumer<IMerkleNode> consumer) {

    }


}
