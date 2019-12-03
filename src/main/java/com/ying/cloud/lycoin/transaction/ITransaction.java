package com.ying.cloud.lycoin.transaction;

import com.ying.cloud.lycoin.merkle.MerkleNode;

public interface ITransaction {
    String getId();
    MerkleNode getMerkleNode();
}
