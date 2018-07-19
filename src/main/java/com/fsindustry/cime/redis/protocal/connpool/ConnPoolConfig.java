package com.fsindustry.cime.redis.protocal.connpool;

import java.net.SocketAddress;

import com.fsindustry.cime.redis.protocal.constant.ChannelType;

import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;

/**
 * 连接池相关配置信息
 *
 * @author fuzhengxin
 * @date 2018/4/19
 */
@Getter
@Setter
public final class ConnPoolConfig {

    /**
     * 标识当前channel是普通连接还是pub/sub连接
     */
    private ChannelType channelType;

    /**
     * 连接服务器地址
     */
    private SocketAddress address;

    /**
     * 命令执行超时时间，单位：毫秒
     */
    private long cmdTimeoutMs;

    /**
     * 重连间隔时间，单位：毫秒
     */
    private long reconnectIntervalMs;

    /**
     * 连接超时时间，单位：毫秒
     */
    private int connectTimeoutMs;

    /**
     * 是否为长连接
     */
    private boolean keepAlive;

    /**
     * 是否禁用nagle
     */
    private boolean tcpNoDelay;

    /**
     * 配置socket实现类
     */
    private Class<? extends Channel> socketChannelClass;
}
