package com.fsindustry.cime.redis.protocal.connpool;

import lombok.Getter;
import lombok.ToString;

/**
 * 回收配置
 *
 * @author fuzhengxin
 * @date 2018/6/5
 */
@ToString
public class EvictionConfig {

    @Getter
    private final long idleEvictTime;

    @Getter
    private final long idleSoftEvictTime;

    /**
     * 最小空闲时间
     */
    @Getter
    private final int minIdle;

    public EvictionConfig(final long poolIdleEvictTime, final long poolIdleSoftEvictTime,
                          final int minIdle) {

        if (poolIdleEvictTime > 0) {
            idleEvictTime = poolIdleEvictTime;
        } else {
            idleEvictTime = Long.MAX_VALUE;
        }

        if (poolIdleSoftEvictTime > 0) {
            idleSoftEvictTime = poolIdleSoftEvictTime;
        } else {
            idleSoftEvictTime = Long.MAX_VALUE;
        }

        this.minIdle = minIdle;
    }
}
