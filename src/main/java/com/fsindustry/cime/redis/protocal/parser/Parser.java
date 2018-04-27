package com.fsindustry.cime.redis.protocal.parser;

import com.fsindustry.cime.redis.protocal.codec.Codec;
import com.fsindustry.cime.redis.protocal.constant.MsgType;

/**
 * 将ByteBuf转换为指定类型对象
 *
 * @author fuzhengxin
 */
public interface Parser<Out> {

    /**
     * 将ByteBuf输入转换为指定对象
     *
     * @param in      输入对象
     * @param msgType 输出对象
     * @param codec   转换对象需用到的解码器
     *
     * @return 输出对象
     */
    Out parse(Object in, MsgType msgType, Codec codec);
}
