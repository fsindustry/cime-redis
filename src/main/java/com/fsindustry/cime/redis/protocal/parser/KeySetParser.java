package com.fsindustry.cime.redis.protocal.parser;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fsindustry.cime.redis.protocal.codec.Codec;
import com.fsindustry.cime.redis.protocal.constant.MsgType;

/**
 * @author fuzhengxin
 * @date 2018/5/7
 */
public class KeySetParser implements Parser<Set<Object>> {

    @Override
    public Set<Object> parse(Object in, MsgType msgType, Codec codec) {

        if (!MsgType.ARRAY.equals(msgType)) {
            throw new UnsupportedOperationException("Unsupported msgType:" + msgType);
        }

        // 获取KEYS解码器
        Codec.Decoder decoder = codec.getKeyDecoder();

        List<byte[]> bufs = (List<byte[]>) in;
        Set<Object> results = new HashSet<>(bufs.size());
        for (byte[] buf : bufs) {
            results.add(decoder.decode(buf));
        }

        return results;
    }
}
