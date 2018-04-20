package com.fsindustry.cime.redis.protocal.conn;

import java.util.concurrent.TimeUnit;

import com.fsindustry.cime.redis.protocal.cmd.Cmd;
import com.fsindustry.cime.redis.protocal.codec.Codec;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;

/**
 * 代表一个redis实例的连接，用于执行redis命令
 *
 * @author fuzhengxin
 */
public interface Connection {

    /**
     * 用于标识存放在channel中的connection的key
     */
    AttributeKey<Connection> CONNECTION_KEY = AttributeKey.valueOf("connection");

    /**
     * 从channel中获取连接对象
     *
     * @param channel 从哪个channel获取
     *
     * @return 具体的Connection对象
     */
    static Connection get(Channel channel) {
        return channel.attr(CONNECTION_KEY).get();
    }

    /**
     * 添加监听器
     *
     * @param listener 监听器对象
     */
    void addListener(ConnectionListener listener);

    /**
     * 删除监听器
     *
     * @param listener 监听器对象
     */
    void removeListener(ConnectionListener listener);

    /**
     * 更新channel
     *
     * @param channel 待更新的channel对象
     */
    void updateChannel(Channel channel);

    /**
     * 初始化连接（异步）
     *
     * @return 初始化后的连接对象future
     */
    Future<Connection> connectAsync();

    /**
     * 初始化连接（同步）
     */
    void connect();

    /**
     * 关闭连接（异步）
     *
     * @return 关闭结果Future
     */
    Future<Void> closeAsync();

    /**
     * 关闭连接（同步）
     */
    void close();

    /**
     * 等待命令执行完成，并返回结果
     *
     * @param future   命令结果future对象
     * @param timeout  超时时间
     * @param timeUnit 时间单位
     * @param <Out>    命令执行结果
     *
     * @return 结果对象 or null
     */
    <Out> Out await(Future<Out> future, long timeout, TimeUnit timeUnit);

    /**
     * 同步执行命令
     *
     * @param cmd    待执行命令
     * @param params 参数
     * @param <Out>  入参/返回值类型
     *
     * @return 执行结果
     */
    <Out> Out sync(Cmd<Out> cmd, Object... params);

    /**
     * 同步执行命令
     *
     * @param codec  编解码器
     * @param cmd    待执行命令
     * @param params 参数
     * @param <In>   输出类型
     * @param <Out>  输出类型参数
     *
     * @return 执行结果
     */
    <In, Out> Out sync(Codec codec, Cmd<In> cmd, Object... params);

    /**
     * 异步执行命令
     *
     * @param cmd    待执行命令
     * @param params 参数
     * @param <In>   输出类型
     * @param <Out>  输出类型参数
     *
     * @return 输出类型future对象
     */
    <In, Out> Future<Out> async(Cmd<In> cmd, Object... params);

    /**
     * 异步执行命令
     *
     * @param timeout 命令执行超时时间
     * @param cmd     待执行命令
     * @param params  参数
     * @param <In>    输出类型
     * @param <Out>   输出类型参数
     *
     * @return 输出类型future对象
     */
    <In, Out> Future<Out> async(long timeout, Cmd<In> cmd, Object... params);

    /**
     * 异步执行命令
     *
     * @param codec  编解码器
     * @param cmd    待执行命令
     * @param params 参数
     * @param <In>   输出类型
     * @param <Out>  输出类型参数
     *
     * @return 输出类型future对象
     */
    <In, Out> Future<Out> async(Codec codec, Cmd<In> cmd, Object... params);

    /**
     * 异步执行命令
     *
     * @param timeout 命令执行超时时间
     * @param codec   编解码器
     * @param cmd     待执行命令
     * @param params  参数
     * @param <In>    输出类型
     * @param <Out>   输出类型参数
     *
     * @return 输出类型future对象
     */
    <In, Out> Future<Out> async(long timeout, Codec codec, Cmd<In> cmd, Object... params);

    /**
     * 判断连接是否可以正常使用
     *
     * @return true 正常； false不正常
     */
    boolean isOk();

    /**
     * 判断连接是否关闭
     *
     * @return true，关闭；false，未关闭；
     */
    boolean isClosed();

    /**
     * 判断连接是否打开
     *
     * @return true 打开
     */
    boolean isOpen();

    /**
     * 判断连接是否建好
     *
     * @return true 建好
     */
    boolean isActive();

    /**
     * 设置连接是否创建成功
     */
    void setSuccess();

    /**
     * 设置连接异常
     *
     * @param cause 异常对象
     */
    void tryFailure(Throwable cause);
}
