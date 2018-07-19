package com.fsindustry.cime.redis.model;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;

import com.fsindustry.cime.redis.model.constant.NodeType;
import com.fsindustry.cime.redis.protocal.conn.Connection;
import com.fsindustry.cime.redis.protocal.conn.ConnectionConfig;

import lombok.Getter;
import lombok.Setter;

/**
 * 针对单个实例的连接池
 *
 * @author fuzhengxin
 * @date 2018/6/22
 */
public class BaseConnectionPool {

    /**
     * 存放空闲连接
     */
    private Queue<Connection> idleConns = new ConcurrentLinkedQueue<>();

    /**
     * 连接池中保有的最小连接数
     */
    private final int minSize;

    /**
     * 连接池中保有的最大连接数
     */
    private final int maxSize;

    /**
     * 空闲连接计数器
     */
    private Semaphore idleCounter;

    /**
     * 标识当前连接池是否被冻结
     */
    @Getter
    private volatile boolean freezed;

    /**
     * 标识连接池被冻结的原因
     */
    @Getter
    @Setter
    private volatile FreezeReason freezeReason;

    /**
     * 标识连接池对应实例的节点类型
     */
    @Getter
    @Setter
    private volatile NodeType nodeType;

    /**
     * 创建连接需要的配置
     */
    private ConnectionConfig config;

    public BaseConnectionPool(final ConnectionConfig config, final int minSize, final int maxSize, final NodeType nodeType) {
        this.config = config;
        this.minSize = minSize;
        this.maxSize = maxSize;
        this.nodeType = nodeType;

        idleCounter = new Semaphore(maxSize);

        // TODO 添加连接监控
    }

    public int idleCount() {
        return idleCounter.availablePermits();
    }

    public int activeCount() {
        return maxSize - idleCount();
    }

    /**
     * 冻结类型
     */
    public enum FreezeReason {
        MANAGER,
        RECONNECT,
        SYSTEM
    }
}
