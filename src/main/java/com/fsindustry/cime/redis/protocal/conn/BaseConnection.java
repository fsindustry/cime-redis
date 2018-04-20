package com.fsindustry.cime.redis.protocal.conn;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.fsindustry.cime.redis.exception.RedisException;
import com.fsindustry.cime.redis.exception.client.ClientConfigException;
import com.fsindustry.cime.redis.exception.client.ClientShutdownException;
import com.fsindustry.cime.redis.exception.server.RedisConnectionException;
import com.fsindustry.cime.redis.exception.server.RedisTimeoutException;
import com.fsindustry.cime.redis.protocal.channel.PlainChannelInitializer;
import com.fsindustry.cime.redis.protocal.channel.PubSubChannelInitializer;
import com.fsindustry.cime.redis.protocal.cmd.Cmd;
import com.fsindustry.cime.redis.protocal.codec.Codec;
import com.fsindustry.cime.redis.protocal.constant.ChannelType;
import com.fsindustry.cime.redis.protocal.future.BasePromise;
import com.fsindustry.cime.redis.protocal.req.BatchCmdReq;
import com.fsindustry.cime.redis.protocal.req.CmdReq;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollDatagramChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.resolver.dns.DnsAddressResolverGroup;
import io.netty.resolver.dns.DnsServerAddressStreamProviders;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.ScheduledFuture;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * redis连接对象基类
 * <p>
 * 实现连接的公用逻辑
 * </p>
 *
 * @author fuzhengxin
 */
@Slf4j
public abstract class BaseConnection implements Connection {

    /**
     * 存放channel初始化bootstrap
     */
    private Bootstrap bootstrap;

    /**
     * 反应堆线程池，共用
     */
    private final EventLoopGroup executors;

    /**
     * 地址解析器，共用
     */
    private DnsAddressResolverGroup resolverGroup;

    /**
     * 存放连接对应的配置信息
     */
    @Getter
    private final ConnectionConfig config;

    /**
     * 连接对应的channel
     */
    @Getter
    private volatile Channel channel;

    /**
     * 标识连接是否关闭
     */
    @Getter
    @Setter
    private volatile boolean closed;

    /**
     * 存放监听
     */
    @Getter
    @Setter
    private List<ConnectionListener> listeners = new ArrayList<>();

    private ConnectionContext ctx;

    /**
     * 最近一次使用时间
     */
    @Getter
    private volatile long lastUsageTime;

    /**
     * 创建时间
     */
    @Getter
    private long createTime;

    BaseConnection(ConnectionConfig config, EventLoopGroup executors) {
        this(config, executors, null);
    }

    BaseConnection(ConnectionConfig config, EventLoopGroup executors,
                   DnsAddressResolverGroup resolverGroup) {
        this.config = config;
        this.executors = executors;

        // 初始化地址解析器
        if (null == resolverGroup) {
            this.resolverGroup = initialResolverGroup(config);
        } else {
            this.resolverGroup = resolverGroup;
        }

        // 初始化bootstrap
        this.bootstrap = initialBootstrap();

        // 初始化上下文对象
        this.ctx = initialCtx();

        // 更新时间戳
        createTime = System.currentTimeMillis();
        lastUsageTime = createTime;
    }

    /**
     * 初始化地址解析器
     *
     * @param config 连接配置参数
     *
     * @return DnsAddressResolverGroup对象
     */
    private DnsAddressResolverGroup initialResolverGroup(ConnectionConfig config) {

        DnsAddressResolverGroup result;
        if (EpollSocketChannel.class.equals(config.getSocketChannelClass())) {
            result = new DnsAddressResolverGroup(EpollDatagramChannel.class,
                    DnsServerAddressStreamProviders.platformDefault());
        } else {
            result = new DnsAddressResolverGroup(NioDatagramChannel.class,
                    DnsServerAddressStreamProviders.platformDefault());
        }

        return result;
    }

    /**
     * 初始化bootstrap
     */
    private Bootstrap initialBootstrap() {

        ChannelInitializer initializer;
        if (ChannelType.PLAIN.equals(config.getChannelType())) {
            initializer = new PlainChannelInitializer();
        } else if (ChannelType.PUB_SUB.equals(config.getChannelType())) {
            initializer = new PubSubChannelInitializer();
        } else {
            throw new ClientConfigException("unknow ChannelType: " + config.getChannelType());
        }

        return new Bootstrap()
                .resolver(this.resolverGroup)
                .channel(config.getSocketChannelClass())
                .group(this.executors)
                .handler(initializer)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, config.getConnectTimeoutMs())
                .option(ChannelOption.SO_KEEPALIVE, config.isKeepAlive())
                .option(ChannelOption.TCP_NODELAY, config.isTcpNoDelay());
    }

    /**
     * 初始化ConnectionContext，用于监听器触发时候传递参数
     */
    private ConnectionContext initialCtx() {
        return ctx = new ConnectionContext(this);
    }

    private void fireClosed() {
        if (listeners.isEmpty()) {
            return;
        }

        // 异步执行监听器动作
        for (ConnectionListener listener : listeners) {
            executors.submit(() -> listener.onClosed(ctx)).addListener(f -> {
                if (!f.isSuccess()) {
                    log.error("execute ConnectionListener.onClosed() error, class: " + listener.getClass(), f.cause());
                }
            });
        }
    }

    private void fireConnected() {
        if (listeners.isEmpty()) {
            return;
        }

        // 异步执行监听器动作
        for (ConnectionListener listener : listeners) {
            executors.submit(() -> listener.onConnected(ctx)).addListener(f -> {
                if (!f.isSuccess()) {
                    log.error("execute ConnectionListener.onConnected() error, class: " + listener.getClass(),
                            f.cause());
                }
            });
        }
    }

    @Override
    public void addListener(ConnectionListener listener) {
        if (null == listener) {
            return;
        }

        listeners.add(listener);
    }

    @Override
    public void removeListener(ConnectionListener listener) {
        if (null == listener) {
            return;
        }

        listeners.remove(listener);
    }

    @Override
    public boolean isOpen() {
        return channel.isOpen();
    }

    @Override
    public boolean isActive() {
        return channel.isActive();
    }

    @Override
    public void setSuccess() {

    }

    @Override
    public void tryFailure(Throwable cause) {

    }

    @Override
    public void updateChannel(Channel channel) {
        this.channel = channel;
        channel.attr(CONNECTION_KEY).set(this);
    }

    @Override
    public Future<Connection> connectAsync() {

        // 触发连接操作，添加监听
        final Promise<Connection> resFuture = new BasePromise<>();
        ChannelFuture channelFuture = bootstrap.connect(config.getAddress());
        channelFuture.addListener(future -> {
            if (future.isSuccess()) {
                resFuture.trySuccess(this);

                // 获取channel对象
                channel = channelFuture.channel();
                channel.attr(Connection.CONNECTION_KEY).set(this);

                // 触发监听器操作
                fireConnected();
            } else {
                resFuture.tryFailure(future.cause());
            }
        });

        return resFuture;
    }

    @Override
    public void connect() {

        // 触发连接操作，同步等待完成
        Future<Connection> future = connectAsync().syncUninterruptibly();

        // 抛出异常
        if (!future.isSuccess()) {
            throw new RedisConnectionException("failed to connect address: "
                    + config.getAddress(), future.cause());
        }
    }

    @Override
    public <R> R await(Future<R> future, long timeout, TimeUnit timeUnit) {

        CountDownLatch latch = new CountDownLatch(1);
        future.addListener((FutureListener<R>) f -> latch.countDown());

        try {

            // 如果超时，则设置结果超时
            if (!latch.await(timeout, timeUnit)) {
                Promise<R> promise = (Promise<R>) future;
                Exception exception = new RedisTimeoutException("Command execution timeout for "
                        + channel.remoteAddress());
                promise.tryFailure(exception);
                throw exception;
            }

            // 执行失败，要抛出异常
            if (!future.isSuccess()) {
                if (future.cause() instanceof RedisException) {
                    throw (RedisException) future.cause();
                }
                throw new RedisException("Unexpected exception while execute command", future.cause());
            }

            return future.getNow();
        } catch (Exception e) {
            // TODO 为什么要中断？
            Thread.currentThread().interrupt();
            return null;
        }
    }

    @Override
    public <Out> Out sync(Cmd<Out> cmd, Object... params) {
        return sync(null, cmd, params);
    }

    @Override
    public <In, Out> Out sync(Codec codec, Cmd<In> cmd, Object... params) {

        // 发起异步命令
        Future<Out> future = async(codec, cmd, params);

        // 等到命令执行完成
        return await(future, config.getCmdTimeoutMs(), TimeUnit.MILLISECONDS);
    }

    @Override
    public <In, Out> Future<Out> async(Cmd<In> cmd, Object... params) {
        return async(null, cmd, params);
    }

    @Override
    public <In, Out> Future<Out> async(long timeout, Cmd<In> cmd, Object... params) {
        return async(timeout, null, cmd, params);
    }

    @Override
    public <In, Out> Future<Out> async(Codec codec, Cmd<In> cmd, Object... params) {
        return async(-1, codec, cmd, params);
    }

    @Override
    public <In, Out> Future<Out> async(long timeout, Codec codec, Cmd<In> cmd, Object... params) {

        Promise<Out> promise = new BasePromise<>();
        if (-1 == timeout) {
            // 配置超时时间
            timeout = config.getCmdTimeoutMs();
        }

        // 如果event关闭，则抛出异常
        if (executors.isShuttingDown()) {
            return BasePromise.newFailedFuture(new ClientShutdownException("client is shutdown"));
        }

        // 添加任务，控制命令超时，不阻塞当前线程
        // 最好使用eventLoopGroup，而非eventLoop，从而使得超时任务和执行命令分散开来
        final ScheduledFuture<?> scheduledFuture = executors.schedule(() -> {
            RedisTimeoutException exception =
                    new RedisTimeoutException("Command execution timeout for " + channel.remoteAddress());
            promise.tryFailure(exception);
        }, timeout, TimeUnit.MILLISECONDS);

        // 添加监听，如果命令完成，则取消超时任务
        promise.addListener((FutureListener<Out>) future -> scheduledFuture.cancel(false));

        // 发送命令数据
        send(new CmdReq<>(promise, cmd, params, codec));
        return promise;
    }

    @Override
    public boolean isOk() {
        return false;
    }

    @Override
    public Future<Void> closeAsync() {

        Promise<Void> resultPromise = new BasePromise<>();

        // 设置状态
        setClosed(true);

        // 触发关闭操作，添加监听
        channel.close().addListeners((FutureListener) future -> {
            if (future.isSuccess()) {
                resultPromise.trySuccess(null);

                // 触发监听器操作
                fireClosed();
            } else {
                resultPromise.tryFailure(future.cause());
            }
        });

        return resultPromise;
    }

    @Override
    public void close() {

        // 触发关闭操作，同步等待完成
        Future<Connection> future = connectAsync().syncUninterruptibly();

        // 抛出异常
        if (!future.isSuccess()) {
            throw new RedisConnectionException("failed to close connection: "
                    + config.getAddress(), future.cause());
        }
    }

    /**
     * 发送单条命令请求
     *
     * @param cmdReq 命令请求
     * @param <In>   输出类型
     * @param <Out>  输出类型参数
     *
     * @return ChannelFuture对象
     */
    <In, Out> ChannelFuture send(CmdReq<In, Out> cmdReq) {
        return channel.writeAndFlush(cmdReq);
    }

    /**
     * 发送多条命令请求
     *
     * @param batchCmdReq 命令请求
     *
     * @return ChannelFuture对象
     */
    ChannelFuture send(BatchCmdReq batchCmdReq) {
        return channel.writeAndFlush(batchCmdReq);
    }

    @Override
    public String toString() {
        return "Connection:" + config.getAddress();
    }
}
