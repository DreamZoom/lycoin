package com.ying.cloud.lycoin.crypto;


import org.apache.commons.codec.digest.DigestUtils;


public class SHA256 {
    public static String encode(String str) {
        return DigestUtils.sha256Hex(str);
    }


}
