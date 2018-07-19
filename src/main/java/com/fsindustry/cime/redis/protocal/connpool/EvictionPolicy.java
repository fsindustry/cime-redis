package com.fsindustry.cime.redis.protocal.connpool;

import com.fsindustry.cime.redis.protocal.conn.Connection;

/**
 * <h1>定义空闲连接回收策略</h1>
 *
 * @author fuzhengxin
 * @date 2018/5/28
 */
public interface EvictionPolicy<T extends Connection> {

    /**
     * 执行回收检查
     *
     * @param config    回收检查相关配置
     * @param underTest 被检查的元素
     * @param idleCount 池中包含的空闲元素，包括当前被检查的元素
     *
     * @return true，应该回收；false，不应该回收；
     */
    boolean evict(EvictionConfig config,
                  PoolElement<T> underTest,
                  int idleCount);
}
