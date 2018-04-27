package com.fsindustry.cime.redis.protocal.parser;

import com.fsindustry.cime.redis.protocal.codec.Codec;
import com.fsindustry.cime.redis.protocal.constant.MsgType;

/**
 * 对输入不做任何处理直接返回
 *
 * @author fuzhengxin
 */
public class EmptyParser<Out> implements Parser<Out> {

    @Override
    public Out parse(Object in, MsgType msgType, Codec codec) {
        return (Out) in;
    }
}
