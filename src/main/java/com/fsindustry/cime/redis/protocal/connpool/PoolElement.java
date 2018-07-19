package com.fsindustry.cime.redis.protocal.connpool;

import java.util.Deque;

import com.fsindustry.cime.redis.protocal.conn.Connection;

/**
 * <h1>连接对象包装器</h1>
 * <p>作为连接池中存放的元素，提供连接对象的状态记录、监控数据统计、比较等能力扩展</p>
 *
 * @author fuzhengxin
 * @date 2018/6/5
 */
public interface PoolElement<T extends Connection> extends Comparable<PoolElement<T>> {

    /**
     * <h2>获取包装器中的连接对象</h2>
     *
     * @return 连接对象
     */
    T get();

    /**
     * <h2>获取创建时间</h2>
     *
     * @return 毫秒时间戳
     */
    long getCreateTime();

    /**
     * <h2>获取对象上一次处于使用状态的时长</h2>
     *
     * @return 使用时长毫秒
     */
    long getActiveTimeMillis();

    /**
     * <h2>获取对象上一次处于空闲状态的时长</h2>
     *
     * @return 使用时长毫秒
     */
    long getIdleTimeMillis();

    /**
     * <h2>获取上一次借用时间</h2>
     *
     * @return 毫秒时间戳
     */
    long getLastBorrowTime();

    /**
     * <h2>获取上一次归还时间</h2>
     *
     * @return 毫秒时间戳
     */
    long getLastReturnTime();

    /**
     * <h2>根据空闲时间比较池中元素</h2>
     *
     * @param other 待比较元素
     *
     * @return {@inheritDoc}
     */
    @Override
    int compareTo(PoolElement<T> other);

    @Override
    boolean equals(Object obj);

    @Override
    int hashCode();

    /**
     * 打印元素信息，用于调试
     *
     * @return 调试信息
     */
    @Override
    String toString();

    /**
     * 使对象变为EVICTION状态
     *
     * @return true，设置成功；false，设置失败；
     */
    boolean startEvictionTest();

    /**
     * <h2>当EVICTION结束后，放入指定的空闲队列</h2>
     *
     * @param idleQueue
     *
     * @return true，设置成功；
     */
    boolean endEvictionTest(Deque<PoolElement<T>> idleQueue);

    /**
     * 设置对象状态
     *
     * @return ture，设置成功
     */
    boolean allocate();

    boolean deallocate();

    void invalidate();

    /**
     * Returns the state of this object.
     *
     * @return state
     */
    ElementState getState();

    /**
     * Marks the pooled object as abandoned.
     */
    void markAbandoned();

    /**
     * Marks the object as returning to the pool.
     */
    void markReturning();
}
