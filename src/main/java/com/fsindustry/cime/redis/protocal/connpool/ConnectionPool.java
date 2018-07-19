package com.fsindustry.cime.redis.protocal.connpool;

import com.fsindustry.cime.redis.protocal.conn.Connection;

/**
 * 连接池定义
 * 用于管理连接对象
 *
 * @author fuzhengxin
 * @date 2018/5/8
 */
public interface ConnectionPool<T extends Connection> {

    /**
     * 从连接池中获取对象
     *
     * @return 连接对象
     */
    T borrow();

    /**
     * 归还对象到连接池
     * <p>每次使用完连接后，归还连接对象</p>
     *
     * @param conn 释放连接对象
     */
    void returN(T conn);

    /**
     * 使连接对象失效
     * <p>发生异常时调用该方法，释放连接对象</p>
     *
     * @param conn
     */
    void invalidate(T conn);

    /**
     * 创建连接对象
     */
    void create();

    /**
     * 获取空闲连接数
     *
     * @return 连接池中空闲的连接数
     */
    int idleCount();

    /**
     * 获取正在使用的连接数量
     *
     * @return 连接池中已经激活的连接数量
     */
    int activeCount();

    /**
     * 清除空闲连接并释放相关资源
     */
    void clear();

    /**
     * 关闭连接池，释放所有连接及相关资源
     */
    void close();
}
