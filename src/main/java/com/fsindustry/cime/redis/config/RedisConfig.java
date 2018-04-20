package com.fsindustry.cime.redis.config;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.concurrent.ExecutorService;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.resolver.dns.DnsAddressResolverGroup;
import io.netty.util.Timer;
import lombok.Getter;
import lombok.Setter;

/**
 * redis实例配置
 *
 * @author fuzhengxin
 */
@Getter
@Setter
public class RedisConfig {

    private URI address;
    private InetSocketAddress addr;

    private Timer timer;
    private ExecutorService executor;
    private EventLoopGroup group;
    private DnsAddressResolverGroup resolverGroup;
    private Class<? extends SocketChannel> socketChannelClass = NioSocketChannel.class;

    /**
     * 连接超时时间
     */
    private int connectTimeout = 10000;

    /**
     * 命令执行超时时间
     */
    private int commandTimeout = 10000;

    /**
     * 实例密码
     */
    private String password;

    /**
     * 数据库
     */
    private int database;

    /**
     * 客户端名称
     */
    private String clientName;

    /**
     * 是否为只读客户端
     */
    private boolean readOnly;

    private boolean keepPubSubOrder = true;

    /**
     * ping间隔
     */
    private int pingConnectionInterval;

    /**
     * 是否为长连接
     */
    private boolean keepAlive;

    /**
     * 是否禁用nagle
     */
    private boolean tcpNoDelay;

    public RedisConfig(RedisConfig src) {
        this.address = src.address;
        this.timer = src.timer;
        this.executor = src.executor;
        this.group = src.group;
        this.socketChannelClass = src.socketChannelClass;
        this.connectTimeout = src.connectTimeout;
        this.commandTimeout = src.commandTimeout;
        this.password = src.password;
        this.database = src.database;
        this.clientName = src.clientName;
        this.readOnly = src.readOnly;
        this.keepPubSubOrder = src.keepPubSubOrder;
        this.pingConnectionInterval = src.pingConnectionInterval;
        this.keepAlive = src.keepAlive;
        this.tcpNoDelay = src.tcpNoDelay;
    }
}
