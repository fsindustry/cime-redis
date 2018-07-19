package com.fsindustry.cime.redis.model;

import com.fsindustry.cime.redis.protocal.connpool.BaseConnPool;

import lombok.Getter;
import lombok.Setter;

/**
 * redis实例
 *
 * @author fuzhengxin
 */
public class Redis implements Operator {

    /**
     * redis实例相关配置，构造实例时传入
     */
    private final RedisConfig config;

    /**
     * 存放redis实例对应的连接池
     */
    private BaseConnPool connectionPool;

    /**
     * 标识当前redis实例是否是master节点
     */
    @Getter
    @Setter
    private volatile boolean master;

    public Redis(RedisConfig config, boolean master) {
        this.config = config;
        this.master = master;
    }

}
