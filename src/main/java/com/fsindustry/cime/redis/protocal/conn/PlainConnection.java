package com.fsindustry.cime.redis.protocal.conn;

import io.netty.channel.EventLoopGroup;
import io.netty.resolver.dns.DnsAddressResolverGroup;

/**
 * 普通redis连接
 * <p>
 * 用于执行普通redis命令的连接
 * </p>
 *
 * @author fuzhengxin
 */
public class PlainConnection extends BaseConnection {

    public PlainConnection(ConnectionConfig config, EventLoopGroup executors) {
        super(config, executors);
    }

    public PlainConnection(ConnectionConfig config, EventLoopGroup executors,
                           DnsAddressResolverGroup resolverGroup) {
        super(config, executors, resolverGroup);
    }
}
