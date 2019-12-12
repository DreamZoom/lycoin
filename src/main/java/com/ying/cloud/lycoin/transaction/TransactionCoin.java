package com.ying.cloud.lycoin.transaction;

import com.ying.cloud.lycoin.crypto.RSAUtils;
import com.ying.cloud.lycoin.crypto.SHA256;
import com.ying.cloud.lycoin.merkle.MerkleDataNode;
import com.ying.cloud.lycoin.merkle.MerkleNode;
import com.ying.cloud.lycoin.models.Account;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class TransactionCoin extends MerkleDataNode implements Transaction {


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

    public TransactionCoin(){
        this.inputs =new ArrayList<>();
        this.outputs =new ArrayList<>();
    }

    public void  addOutput(TransactionOut out){
        outputs.add(out);
    }

    public void sign(Account account) throws Exception{
        StringBuffer sb =new StringBuffer();

        for (int i = 0; i < inputs.size(); i++) {
            sb.append(inputs.get(i).getId()+inputs.get(i).getIndex());
        }
        for (int i = 0; i < outputs.size(); i++) {
            sb.append(outputs.get(i).getAddress()+outputs.get(i).getAmount());
        }

        this.id = SHA256.encode(sb.toString());

        signature = RSAUtils.sign(id+sb.toString(),account.getPrivateKey());
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public MerkleNode getMerkleNode() {
        return this;
    }

    @Override
    public String toHashString() {
        StringBuffer sb =new StringBuffer();

        for (int i = 0; i < inputs.size(); i++) {
            sb.append(inputs.get(i).getId()+inputs.get(i).getIndex());
        }
        for (int i = 0; i < outputs.size(); i++) {
            sb.append(outputs.get(i).getAddress()+outputs.get(i).getAmount());
        }
        return SHA256.encode(sb.toString());
    }
}
