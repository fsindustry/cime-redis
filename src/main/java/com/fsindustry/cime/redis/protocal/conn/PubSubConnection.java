package com.fsindustry.cime.redis.protocal.conn;

import io.netty.channel.EventLoopGroup;
import io.netty.resolver.dns.DnsAddressResolverGroup;

/**
 * pub/sub连接
 *
 * @author fuzhengxin
 */
public class PubSubConnection extends PlainConnection {

    public PubSubConnection(ConnectionConfig config, EventLoopGroup executors) {
        super(config, executors);
    }

    public PubSubConnection(ConnectionConfig config, EventLoopGroup executors,
                            DnsAddressResolverGroup resolverGroup) {
        super(config, executors, resolverGroup);
    }
}
