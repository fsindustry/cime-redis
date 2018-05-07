package com.fsindustry.cime.redis.protocal.conn;

import java.net.InetSocketAddress;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.fsindustry.cime.redis.protocal.cmd.Cmds;
import com.fsindustry.cime.redis.protocal.codec.StringCodec;
import com.fsindustry.cime.redis.protocal.constant.ChannelType;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

/**
 * PlainConnection测试类
 */
@Slf4j
public class PlainConnectionTest {

    private PlainConnection connection;

    @Before
    public void setUp() throws Exception {

        // 初始化配置
        ConnectionConfig connectionConfig = new ConnectionConfig();
        connectionConfig.setChannelType(ChannelType.PLAIN);
        connectionConfig.setAddress(new InetSocketAddress("127.0.0.1", 6379));
        connectionConfig.setCmdTimeoutMs(60 * 1000);
        connectionConfig.setReconnectIntervalMs(10 * 1000);
        connectionConfig.setConnectTimeoutMs(30 * 1000);
        connectionConfig.setKeepAlive(true);
        connectionConfig.setTcpNoDelay(true);
        connectionConfig.setSocketChannelClass(NioSocketChannel.class);

        // 初始化反应堆线程池
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        // 创建连接对象
        connection = new PlainConnection(connectionConfig, eventLoopGroup);
    }

    @Test
    public void addListener() throws Exception {
    }

    @Test
    public void removeListener() throws Exception {
    }

    @Test
    public void isOpen() throws Exception {
    }

    @Test
    public void isActive() throws Exception {
    }

    @Test
    public void setSuccess() throws Exception {
    }

    @Test
    public void tryFailure() throws Exception {
    }

    @Test
    public void updateChannel() throws Exception {
    }

    @Test
    public void connectAsync() throws Exception {

        // 异步初始化连接
        Promise<Connection> connectionFuture = (Promise<Connection>) connection.connectAsync();
        Connection connection1 = connectionFuture.get();
        Assert.assertNotNull(connection1);
        Assert.assertTrue(connection == connection1);
        ping();
    }

    @Test
    public void connect() throws Exception {

        // 同步初始化连接
        connection.connect();

        ping();
    }

    @Test
    public void await() throws Exception {
    }

    @Test
    public void sync() throws Exception {
    }

    @Test
    public void sync1() throws Exception {
    }

    @Test
    public void async() throws Exception {
        connection.connect();

        // 执行PING命令
        Promise<String> promise = (Promise<String>) connection.async(Cmds.PING);
        Assert.assertEquals("PONG", promise.get());
    }

    @Test
    public void async1() throws Exception {
    }

    @Test
    public void async2() throws Exception {
    }

    @Test
    public void async3() throws Exception {
    }

    @Test
    public void isOk() throws Exception {
    }

    @Test
    public void closeAsync() throws Exception {
    }

    @Test
    public void close() throws Exception {
    }

    @Test
    public void send() throws Exception {
    }

    @Test
    public void send1() throws Exception {
    }

    @Test
    public void setClosed() throws Exception {
    }

    @Test
    public void setListeners() throws Exception {
    }

    @Test
    public void getConfig() throws Exception {
    }

    @Test
    public void getChannel() throws Exception {
    }

    @Test
    public void isClosed() throws Exception {
    }

    @Test
    public void getListeners() throws Exception {
    }

    @Test
    public void getLastUsageTime() throws Exception {
    }

    @Test
    public void getCreateTime() throws Exception {
    }

    @Test
    public void ping() throws Exception {
        connection.connect();
        Future<String> result = connection.ping();
        Assert.assertEquals("PONG", result.get());
    }

    @Test
    public void keys() throws Exception {
        connection.connect();
        Future<Set<Object>> result = connection.keys("*".getBytes(CharsetUtil.UTF_8), new StringCodec());

        System.out.println(result.get());

    }
}