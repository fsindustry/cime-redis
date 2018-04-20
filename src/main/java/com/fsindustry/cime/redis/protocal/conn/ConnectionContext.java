package com.fsindustry.cime.redis.protocal.conn;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Connection上下文
 * <p>
 * 用于存放监听触发时需要的连接信息
 *
 * @author fuzhengxin
 */
@Getter
@AllArgsConstructor
public class ConnectionContext {

    /**
     * 存放连接对象
     */
    private Connection connection;
}
