package com.fsindustry.cime.redis.protocal.codec;

/**
 * @author fuzhengxin
 * @date 2018/5/7
 */
public class StringCodec implements Codec {

    private StringEncoder encoder = new StringEncoder();

    private StringDecoder decoder = new StringDecoder();

    @Override
    public Decoder getKeyDecoder() {
        return decoder;
    }

    @Override
    public Encoder getKeyEncoder() {
        return encoder;
    }

    @Override
    public Decoder getValueDecoder() {
        return decoder;
    }

    @Override
    public Encoder getValueEncoder() {
        return encoder;
    }

    @Override
    public Decoder getMapValueDecoder() {
        return decoder;
    }

    @Override
    public Encoder getMapValueEncoder() {
        return encoder;
    }

    @Override
    public Decoder getMapKeyDecoder() {
        return decoder;
    }

    @Override
    public Encoder getMapKeyEncoder() {
        return encoder;
    }

    @Override
    public ClassLoader getClassLoader() {
        return this.getClassLoader();
    }
}
