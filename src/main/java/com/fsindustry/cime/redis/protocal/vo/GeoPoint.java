package com.fsindustry.cime.redis.protocal.vo;

import lombok.Getter;

/**
 * 存放Geo经纬度
 *
 * @author fuzhengxin
 */
@Getter
public final class GeoPoint {

    /**
     * 经度
     */
    private final Double longitude;

    /**
     * 纬度
     */
    private final Double latitude;

    public GeoPoint(Double longitude, Double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }
}
