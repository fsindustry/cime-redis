package com.fsindustry.cime.redis.protocal.handler;

import com.fsindustry.cime.redis.protocal.vo.BatchCmdReq;
import com.fsindustry.cime.redis.protocal.vo.CmdReq;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * 多条命令编码器
 *
 * @author fuzhengxin
 */
@Slf4j
@ChannelHandler.Sharable
public class BatchCmdEncoder extends MessageToByteEncoder<BatchCmdReq> {

    /**
     * 私有化构造器
     */
    private BatchCmdEncoder() {
    }

    /**
     * 懒汉式单实例
     */
    private static class Singleton {
        private final static BatchCmdEncoder INSTANCE = new BatchCmdEncoder();
    }

    /**
     * 获取单例
     */
    public static BatchCmdEncoder instance() {
        return Singleton.INSTANCE;
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {

        if (acceptOutboundMessage(msg)) {
            if (!promise.setUncancellable()) {
                return;
            }
        }

        super.write(ctx, msg, promise);
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, BatchCmdReq msg, ByteBuf out) throws Exception {

        CmdEncoder encoder = ctx.pipeline().get(CmdEncoder.class);
        for (CmdReq<?, ?> cmdReq : msg.getCmds()) {
            encoder.encode(ctx, cmdReq, out);
        }
    }
}
