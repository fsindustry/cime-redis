package com.fsindustry.cime.redis.protocal.codec;

import io.netty.util.CharsetUtil;

/**
 * @author fuzhengxin
 * @date 2018/5/7
 */
public class StringDecoder implements Codec.Decoder<String> {

    @Override
    public String decode(byte[] data) {
        return new String(data, CharsetUtil.UTF_8);
    }
}
