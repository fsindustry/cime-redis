package com.fsindustry.cime.redis.protocal.connpool;

import com.fsindustry.cime.redis.protocal.conn.Connection;

/**
 * <h1>用于创建连接池元素的工厂接口</h1>
 *
 * @param <T> 连接对象类型
 *
 * @author fuzhengxin
 * @date 2018/6/7
 */
public interface PoolElementFactory<T extends Connection> {

    /**
     * 创建连接
     *
     * @return 通过{@code PoolElement}包装的连接对象
     *
     * @throws Exception 创建连接异常
     */
    PoolElement<T> create() throws Exception;

    /**
     * 销毁连接
     *
     * @param p 待销毁的{@code PoolElement}
     *
     * @throws Exception 销毁连接产生的异常
     */
    void destroy(PoolElement<T> p) throws Exception;

    /**
     * 校验连接，确保连接安全放入池中
     *
     * @param p 待校验的{@code PooledObject}对象
     *
     * @return false，连接不正常，需要从池中移除；true，连接正常；
     */
    boolean validate(PoolElement<T> p);

    /**
     * 激活一个连接，在从池中取出之前
     *
     * @param p 待激活的{@code PoolElement}对象
     *
     * @throws Exception 激活过程产生的异常
     */
    void activate(PoolElement<T> p) throws Exception;

    /**
     * 钝化一个连接，在放入池中之前
     *
     * @param p 待钝化的{@code PoolElement}对象
     *
     * @throws Exception 钝化过程产生的异常
     */
    void passivate(PoolElement<T> p) throws Exception;
}
