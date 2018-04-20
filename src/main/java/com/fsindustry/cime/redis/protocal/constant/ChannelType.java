package com.fsindustry.cime.redis.protocal.constant;

/**
 * 标识redis的channel类型：
 * 普通渠道：PLAIN；
 * 订阅/发布渠道：pub/sub；
 *
 * @author fuzhengxin
 */
public enum ChannelType {

    /**
     * 普通渠道
     */
    PLAIN,

    /**
     * 订阅-发布渠道
     */
    PUB_SUB;
}
