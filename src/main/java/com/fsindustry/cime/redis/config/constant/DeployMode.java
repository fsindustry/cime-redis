package com.fsindustry.cime.redis.config.constant;

/**
 * redis部署模式，分为3种：
 * 集群模式；
 * 哨兵模式；
 * 主从模式；
 *
 * @author fuzhengxin
 */
public enum DeployMode {

    /**
     * 集群模式
     */
    CLUSTER,

    /**
     * 哨兵模式
     */
    SENTINEL,

    /**
     * 主从模式
     */
    MASTER_SLAVE;
}
