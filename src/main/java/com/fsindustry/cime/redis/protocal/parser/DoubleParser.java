package com.fsindustry.cime.redis.protocal.parser;

import com.fsindustry.cime.redis.protocal.codec.Codec;
import com.fsindustry.cime.redis.protocal.constant.MsgType;

import io.netty.util.CharsetUtil;

/**
 * 将输入对象转换为double值
 *
 * @author fuzhengxin
 */
public class DoubleParser implements Parser<Double> {

    @Override
    public Double parse(Object in, MsgType msgType, Codec codec) {

        // 如果是二进制字符串，则转码成字符串
        if (MsgType.BULK_STRING.equals(msgType)
                && in instanceof byte[]) {

            String value = new String((byte[]) in, CharsetUtil.UTF_8);
            return Double.valueOf(value);
        } else {
            throw new UnsupportedOperationException("Unsupported msgType:" + msgType);
        }
    }
}
