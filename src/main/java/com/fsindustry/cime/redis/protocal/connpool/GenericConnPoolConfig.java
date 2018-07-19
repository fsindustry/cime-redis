package com.fsindustry.cime.redis.protocal.connpool;

import lombok.Getter;
import lombok.Setter;

/**
 * <h1>通用连接池配置对象</h1>
 *
 * @author fuzhengxin
 * @date 2018/6/7
 */
public class GenericConnPoolConfig extends BaseConnPoolConfig {

    /**
     * The default value for the {@code maxTotal} configuration attribute.
     *
     * @see GenericObjectPool#getMaxTotal()
     */
    public static final int DEFAULT_MAX_TOTAL = 8;

    /**
     * The default value for the {@code maxIdle} configuration attribute.
     *
     * @see GenericObjectPool#getMaxIdle()
     */
    public static final int DEFAULT_MAX_IDLE = 8;

    /**
     * The default value for the {@code minIdle} configuration attribute.
     *
     * @see GenericObjectPool#getMinIdle()
     */
    public static final int DEFAULT_MIN_IDLE = 0;

    @Getter
    @Setter
    private int maxTotal = DEFAULT_MAX_TOTAL;

    @Getter
    @Setter
    private int maxIdle = DEFAULT_MAX_IDLE;

    @Getter
    @Setter
    private int minIdle = DEFAULT_MIN_IDLE;
}
