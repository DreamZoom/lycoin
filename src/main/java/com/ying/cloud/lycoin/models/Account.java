package com.ying.cloud.lycoin.models;

import com.ying.cloud.lycoin.store.StoreUtils;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import java.io.Serializable;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class Account implements Serializable {
    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    private String publicKey;
    private String privateKey;

    public static Account generate() throws Exception{
        Account account =new Account();
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        // 初始化密钥对生成器，密钥大小为96-1024位
        keyPairGen.initialize(1024,new SecureRandom());
        // 生成一个密钥对，保存在keyPair中
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();   // 得到私钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();  // 得到公钥
        String publicKeyString = new String(Base64.encodeBase64(publicKey.getEncoded()));
        // 得到私钥字符串
        String privateKeyString = new String(Base64.encodeBase64((privateKey.getEncoded())));

        account.setPublicKey(publicKeyString);
        account.setPrivateKey(privateKeyString);
        return account;
    }

    public static Account init() throws Exception{
        try{
            Account account = StoreUtils.load(Account.class);
            if(account==null){
                account=Account.generate();
                StoreUtils.save(account);
            }
            return account;
        }catch (Exception err){
            System.out.println(err.getMessage());
        }
        return null;
    }

}
