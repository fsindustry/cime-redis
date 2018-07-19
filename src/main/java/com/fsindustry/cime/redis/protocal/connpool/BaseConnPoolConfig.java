package com.fsindustry.cime.redis.protocal.connpool;

import lombok.Getter;
import lombok.Setter;

/**
 * <h1>存放连接池对应的共用配置</h1>
 *
 * @author fuzhengxin
 * @date 2018/6/8
 */
@Getter
@Setter
public abstract class BaseConnPoolConfig {

    /*-----------------------------常量定义--------------------------------------*/

    public static final int DEFAULT_MAX_TOTAL = -1;
    public static final boolean DEFAULT_LIFO = true;
    public static final boolean DEFAULT_FAIRNESS = false;
    public static final long DEFAULT_MAX_WAIT_MILLIS = -1L;
    public static final long DEFAULT_MIN_EVICTABLE_IDLE_TIME_MILLIS =
            1000L * 60L * 30L;
    public static final long DEFAULT_SOFT_MIN_EVICTABLE_IDLE_TIME_MILLIS = -1;
    public static final long DEFAULT_EVICTOR_SHUTDOWN_TIMEOUT_MILLIS =
            10L * 1000L;
    public static final int DEFAULT_NUM_TESTS_PER_EVICTION_RUN = 3;
    public static final boolean DEFAULT_TEST_ON_CREATE = false;
    public static final boolean DEFAULT_TEST_ON_BORROW = false;
    public static final boolean DEFAULT_TEST_ON_RETURN = false;
    public static final boolean DEFAULT_TEST_WHILE_IDLE = false;
    public static final long DEFAULT_TIME_BETWEEN_EVICTION_RUNS_MILLIS = -1L;
    public static final boolean DEFAULT_BLOCK_WHEN_EXHAUSTED = true;
    public static final String DEFAULT_EVICTION_POLICY_CLASS_NAME =
            "org.apache.commons.pool2.impl.DefaultEvictionPolicy";

    /*-----------------------------配置属性定义--------------------------------------*/

    /**
     * 是否使用后进先出策略
     */
    private boolean lifo = DEFAULT_LIFO;

    /**
     * 是否使用公平策略
     */
    private boolean fairness = DEFAULT_FAIRNESS;

    /**
     * 如果没有空闲连接，最大等待时间
     */
    private long maxWaitMillis = DEFAULT_MAX_WAIT_MILLIS;

    private long minEvictableIdleTimeMillis = DEFAULT_MIN_EVICTABLE_IDLE_TIME_MILLIS;

    private long evictorShutdownTimeoutMillis = DEFAULT_EVICTOR_SHUTDOWN_TIMEOUT_MILLIS;

    private long softMinEvictableIdleTimeMillis = DEFAULT_SOFT_MIN_EVICTABLE_IDLE_TIME_MILLIS;

    private int numTestsPerEvictionRun = DEFAULT_NUM_TESTS_PER_EVICTION_RUN;

    private String evictionPolicyClassName = DEFAULT_EVICTION_POLICY_CLASS_NAME;

    private boolean testOnCreate = DEFAULT_TEST_ON_CREATE;
    private boolean testOnBorrow = DEFAULT_TEST_ON_BORROW;
    private boolean testOnReturn = DEFAULT_TEST_ON_RETURN;
    private boolean testWhileIdle = DEFAULT_TEST_WHILE_IDLE;

    private long timeBetweenEvictionRunsMillis = DEFAULT_TIME_BETWEEN_EVICTION_RUNS_MILLIS;

    private boolean blockWhenExhausted = DEFAULT_BLOCK_WHEN_EXHAUSTED;
}
