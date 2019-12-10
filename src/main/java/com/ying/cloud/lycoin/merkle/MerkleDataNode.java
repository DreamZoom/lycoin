package com.ying.cloud.lycoin.merkle;

import java.util.function.Consumer;

public class MerkleDataNode extends MerkleNode {
    @Override
    public void iterator(Consumer<IMerkleNode> consumer) {
        consumer.accept(this);
    }
}
