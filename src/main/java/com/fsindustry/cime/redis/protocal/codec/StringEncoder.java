package com.fsindustry.cime.redis.protocal.codec;

import io.netty.util.CharsetUtil;

/**
 * @author fuzhengxin
 * @date 2018/5/7
 */
public class StringEncoder implements Codec.Encoder {
    @Override
    public byte[] encode(Object in) {
        return in.toString().getBytes(CharsetUtil.UTF_8);
    }
}
