package com.fsindustry.cime.redis.protocal.conn;

import java.util.EventListener;

/**
 * Connection监听器定义
 *
 * @author fuzhengxin
 */
public interface ConnectionListener extends EventListener {

    /**
     * 连接后调用的方法
     *
     * @param ctx 连接上下文
     */
    void onConnected(ConnectionContext ctx);

    /**
     * 断连后调用的方法
     *
     * @param ctx 连接上下文
     */
    void onClosed(ConnectionContext ctx);

}
