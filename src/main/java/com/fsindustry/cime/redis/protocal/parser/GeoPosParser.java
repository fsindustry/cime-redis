package com.fsindustry.cime.redis.protocal.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.fsindustry.cime.redis.protocal.codec.Codec;
import com.fsindustry.cime.redis.protocal.constant.MsgType;
import com.fsindustry.cime.redis.protocal.vo.GeoPoint;

import io.netty.util.CharsetUtil;

/**
 * geopos命令解析
 *
 * @author fuzhengxin
 */
public class GeoPosParser implements Parser<List<GeoPoint>> {

    @Override
    public List<GeoPoint> parse(Object in, MsgType msgType, Codec codec) {

        // 如果是普通字符串，直接返回
        if (MsgType.ARRAY.equals(msgType)
                && in instanceof String) {

            List<Object> level1List = (List<Object>) in;
            if (level1List.isEmpty()) {
                return null;
            }

            List<GeoPoint> results = new ArrayList<>(level1List.size());
            for (Object level1 : level1List) {

                // 如果结果不合法，则跳过
                if (!(level1 instanceof Collection)) {
                    continue;
                }

                List<byte[]> level2List = (List<byte[]>) level1;
                if (level2List.size() != 2) {
                    // TODO 抛出异常
                    continue;
                }

                // 解析经纬度
                double longitude = Double.valueOf(new String(level2List.get(0), CharsetUtil.UTF_8));
                double latitude = Double.valueOf(new String(level2List.get(1), CharsetUtil.UTF_8));
                results.add(new GeoPoint(longitude, latitude));
            }

            return results;
        } else {
            throw new UnsupportedOperationException("Unsupported msgType:" + msgType);
        }
    }
}
