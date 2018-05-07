package com.fsindustry.cime.redis.protocal.parser;

import java.util.ArrayList;
import java.util.List;

import com.fsindustry.cime.redis.protocal.codec.Codec;
import com.fsindustry.cime.redis.protocal.constant.MsgType;

import io.netty.util.CharsetUtil;

/**
 * georadius命令解析
 *
 * @author fuzhengxin
 */
public class StringListParser implements Parser<List<String>> {

    @Override
    public List<String> parse(Object in, MsgType msgType, Codec codec) {

        // 如果是普通字符串，直接返回
        if (MsgType.ARRAY.equals(msgType)
                && in instanceof String) {

            List<byte[]> byteBufs = (List<byte[]>) in;
            if (byteBufs.isEmpty()) {
                return null;
            }

            List<String> results = new ArrayList<>(byteBufs.size());
            for (byte[] byteBuf : byteBufs) {
                results.add(new String(byteBuf, CharsetUtil.UTF_8));
            }

            return results;
        } else {
            throw new UnsupportedOperationException("Unsupported msgType:" + msgType);
        }
    }
}
