package com.fsindustry.cime.redis.protocal.parser;

import com.fsindustry.cime.redis.protocal.codec.Codec;
import com.fsindustry.cime.redis.protocal.constant.MsgType;

/**
 * 将输入对象转换为整型
 *
 * @author fuzhengxin
 */
public class IntegerParser implements Parser<Integer> {

    @Override
    public Integer parse(Object in, MsgType msgType, Codec codec) {

        if (!MsgType.INTEGER.equals(msgType)
                || !(in instanceof Long)) {
            throw new UnsupportedOperationException("Unsupported msgType:" + msgType);
        }

        // 如果是普通字符串，直接返回
        return ((Long) in).intValue();
    }
}
