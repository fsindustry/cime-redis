package com.fsindustry.cime.redis.protocal.parser;

import com.fsindustry.cime.redis.protocal.codec.Codec;
import com.fsindustry.cime.redis.protocal.constant.MsgType;

import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;

/**
 * 将输入对象转换为字符串
 *
 * @author fuzhengxin
 */
public class StringParser implements Parser<String> {

    @Override
    public String parse(Object in, MsgType msgType, Codec codec) {
        // 如果是普通字符串，直接返回
        if (MsgType.STRING.equals(msgType)
                && in instanceof String) {
            return (String) in;
        }
        // 如果是二进制字符串，则转码成字符串
        else if (MsgType.BULK_STRING.equals(msgType)
                && in instanceof ByteBuf) {

            return ((ByteBuf) in).toString(CharsetUtil.UTF_8);
        } else {
            throw new UnsupportedOperationException("Unsupported msgType:" + msgType);
        }
    }
}
