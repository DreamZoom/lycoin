package com.ying.cloud.lycoin.merkle;

public interface IMerkleNode {

    String toHashString();
    boolean verify();
    void encode();
}
