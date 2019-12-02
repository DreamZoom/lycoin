package com.ying.cloud.lycoin.transaction;

import com.ying.cloud.lycoin.crypto.SHA256;
import com.ying.cloud.lycoin.merkle.MerkleDataNode;
import com.ying.cloud.lycoin.models.Account;

import java.util.List;

public class Transaction extends MerkleDataNode implements ITransaction {
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<TransactionIn> getInputs() {
        return inputs;
    }

    public void setInputs(List<TransactionIn> inputs) {
        this.inputs = inputs;
    }

    public List<TransactionOut> getOutputs() {
        return outputs;
    }

    public void setOutputs(List<TransactionOut> outputs) {
        this.outputs = outputs;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    private String id;
    private List<TransactionIn> inputs;
    private List<TransactionOut> outputs;
    private String signature;

    public void  addOutput(TransactionOut out){
        outputs.add(out);
    }

    public void sign(Account account){
        //SHA256.encode()
    }
}
