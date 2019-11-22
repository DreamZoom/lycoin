package com.ying.cloud.lycoin.crypto;

import org.apache.commons.codec.digest.DigestUtils;

public class DefaultHashEncoder implements HashEncoder {
    @Override
    public String encode(String raw) {
        return DigestUtils.sha256Hex(raw);
    }
}
