package com.fsindustry.cime.redis.protocal.codec;

import io.netty.buffer.ByteBuf;

/**
 * 数据编解码器
 *
 * @author fuzhengxin
 */
public interface Codec {

    /**
     * 除hash表以外的其它数据类型使用的解码器
     *
     * @return decoder 解码器
     */
    Decoder<Object> getValueDecoder();

    /**
     * 除hash表以外的其它数据类型使用的编码器
     *
     * @return encoder
     */
    Encoder getValueEncoder();

    /**
     * hash表value使用的解码器
     *
     * @return decoder
     */
    Decoder<Object> getMapValueDecoder();

    /**
     * hash表value使用的编码器
     *
     * @return encoder
     */
    Encoder getMapValueEncoder();

    /**
     * hash表value使用的解码器
     *
     * @return decoder
     */
    Decoder<Object> getMapKeyDecoder();

    /**
     * hash表value使用的编码器
     *
     * @return encoder
     */
    Encoder getMapKeyEncoder();

    /**
     * 用于加载Decoder用到的类
     *
     * @return class loader
     */
    ClassLoader getClassLoader();

    /**
     * 编码器接口
     */
    interface Encoder {

        /**
         * 将对象编码后放入ByteBuf
         *
         * @param in 待编码的对象
         *
         * @return ByteBuf
         */
        ByteBuf encode(Object in);
    }

    /**
     * 解码器接口
     *
     * @param <Out> 解码后的对象
     */
    interface Decoder<Out> {

        /**
         * 将ByteBuf中的数据解码变为具体的对象
         *
         * @param buf 存放数据的ByteBuf对象
         *
         * @return 解码后的对象
         */
        Out decode(ByteBuf buf);
    }
}
