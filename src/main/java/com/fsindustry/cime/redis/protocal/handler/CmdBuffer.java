package com.fsindustry.cime.redis.protocal.handler;

import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

import com.fsindustry.cime.redis.protocal.vo.BufferedReq;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.AttributeKey;
import io.netty.util.internal.PlatformDependent;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 用于缓存请求命令，异步匹配响应结果
 * <p>
 * 问：RESP协议中并没有消息序列号，如何保证异步请求和响应的能够匹配？
 * <p>
 * 1）一定是一个channel对应一个CmdBuffer；
 * 2）消息的匹配利用了redis单线程执行命令的顺序性；
 * 3）对于同一个channel而言，所有命令均按请求的时间顺序存入队列，
 * 队列头部的第一个元素一定是当前channel的第一个请求；
 * 4）由于redis实例使用单线程反应堆模型处理命令，因而对于同一个channel的请求一定是按照请求顺序处理并返回的；
 * 根据以上特性，channel先发出的消息一定是先响应，可以保证异步请求与响应能够按照时间顺序匹配；
 *
 * @author fuzhengxin
 * @date 2018/4/23
 */
@Slf4j
public class CmdBuffer extends ChannelDuplexHandler {

    /**
     * channel中存放待执行命令的key
     */
    static final AttributeKey<BufferedReq> CURRENT_COMMAND = AttributeKey.valueOf("currentCommand");

    /**
     * 使用MPSC队列缓存当前channel待处理的命令
     */
    private final Queue<Holder> queue = PlatformDependent.newMpscQueue();

    /**
     * 发送消息失败的监听器，避免重复创建，共用一个；
     */
    private final ChannelFutureListener listener = future -> {
        // 如果命令执行失败，则执行下一条命令
        if (!future.isSuccess() && future.channel().isActive()) {
            sendNext(future.channel());
        }
    };

    /**
     * 发送消息到下一环节
     *
     * @param channel 发送消息的channel
     */
    private void send(Channel channel) {

        Holder holder = queue.peek();
        if (holder != null && holder.trySend()) {
            // 发送消息
            BufferedReq req = holder.getReq();
            channel.attr(CURRENT_COMMAND).set(req);
            holder.getChannelPromise().addListener(listener);
            channel.writeAndFlush(req, holder.getChannelPromise());
        }
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {

        // 如果消息不需要缓存，则直接进入下一环节
        if (!(msg instanceof BufferedReq)) {
            ctx.write(msg, promise);
            return;
        }

        // 如果是重复请求，则直接进入下一环节
        Holder holder = queue.peek();
        if (holder != null && holder.getReq() == msg) {
            ctx.write(holder.getReq(), promise);
            return;
        }

        // 缓存消息
        queue.add(new Holder(promise, (BufferedReq) msg));

        // 发送消息
        send(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // TODO 检查是否需要完善异常处理
        log.error("Exception occured. Channel: " + ctx.channel(), cause);
    }

    /**
     * 发送下一条消息到下一环节
     *
     * @param channel 发送消息的channel
     */
    public void sendNext(Channel channel) {
        channel.attr(CURRENT_COMMAND).set(null);
        queue.poll();
        send(channel);
    }

    /**
     * 存放请求命令及相关信息
     *
     * @author fuzhengxin
     * @date 2018/4/23
     */
    @Getter
    private static final class Holder {

        /**
         * 标识消息是否已经发送
         */
        private final AtomicBoolean sended = new AtomicBoolean(false);

        private final ChannelPromise channelPromise;

        private final BufferedReq req;

        private Holder(ChannelPromise channelPromise, BufferedReq req) {
            this.channelPromise = channelPromise;
            this.req = req;
        }

        /**
         * 设置发送状态
         *
         * @return true，设置成功；false，设置失败；
         */
        private boolean trySend() {
            return this.sended.compareAndSet(false, true);
        }
    }
}
