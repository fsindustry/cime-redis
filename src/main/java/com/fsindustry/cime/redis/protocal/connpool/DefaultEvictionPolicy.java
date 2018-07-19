package com.fsindustry.cime.redis.protocal.connpool;

import com.fsindustry.cime.redis.protocal.conn.Connection;

/**
 * <h1>EvictionPolicy的默认实现</h1>
 *
 * @author fuzhengxin
 * @date 2018/6/5
 */
public class DefaultEvictionPolicy<T extends Connection> implements EvictionPolicy {

    @Override
    public boolean evict(EvictionConfig config, PoolElement underTest, int idleCount) {

        if (underTest.getIdleTimeMillis() > config.getIdleSoftEvictTime()
                && idleCount > config.getMinIdle()) {
            return true;
        }

        if (underTest.getIdleTimeMillis() > config.getIdleEvictTime()) {
            return true;
        }

        return false;
    }

}
