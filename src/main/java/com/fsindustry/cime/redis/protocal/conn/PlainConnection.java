package com.fsindustry.cime.redis.protocal.conn;

import java.util.Set;

import com.fsindustry.cime.redis.protocal.cmd.Cmds;
import com.fsindustry.cime.redis.protocal.codec.Codec;

import io.netty.channel.EventLoopGroup;
import io.netty.resolver.dns.DnsAddressResolverGroup;
import io.netty.util.concurrent.Future;

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

    @Override
    public Future<String> ping() {
        return async(Cmds.PING);
    }

    @Override
    public Future<Set<Object>> keys(byte[] pattern, Codec codec) {
        return async(codec, Cmds.KEYS, (Object) pattern);
    }
}
