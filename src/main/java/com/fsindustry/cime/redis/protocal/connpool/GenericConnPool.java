package com.fsindustry.cime.redis.protocal.connpool;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.fsindustry.cime.redis.protocal.conn.Connection;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 通用连接池实现
 *
 * @author fuzhengxin
 * @date 2018/5/8
 */
@ToString
public class GenericConnPool<T extends Connection> extends BaseConnPool<T> {

    /*-------------------------------成员变量------------------------------------*/

    /**
     * 存放连接池的空闲连接
     */
    private final LinkedBlockingDeque<PoolElement<T>> idleElements;

    /**
     * 连接池允许的最大空闲连接数
     */
    @Getter
    @Setter
    private volatile int maxIdle = GenericConnPoolConfig.DEFAULT_MAX_IDLE;

    /**
     * 连接池允许的最小空闲连接数
     */
    @Getter
    @Setter
    private volatile int minIdle = GenericConnPoolConfig.DEFAULT_MIN_IDLE;

    /**
     * 管理池对象的工厂
     */
    @Getter
    private final PoolElementFactory<T> factory;

    /**
     * 创建连接时加锁
     */
    private Lock createLock = new ReentrantLock();

    private Condition createCondition = createLock.newCondition();

    /**
     * 创建连接的数量
     */
    private final AtomicLong createCount = new AtomicLong(0);

    private long makeObjectCount = 0;

    private final Map<IdentityWrapper<T>, PooledObject<T>> allObjects =
            new ConcurrentHashMap<IdentityWrapper<T>, PooledObject<T>>();



    /*-------------------------------构造器------------------------------------*/

    /**
     * 使用默认配置创建连接池
     *
     * @param factory 连接池初始化工厂
     */
    public GenericConnPool(PoolElementFactory<T> factory) {
        // 使用默认配置
        this(factory, new GenericConnPoolConfig());
    }

    /**
     * 使用指定配置创建连接池
     *
     * @param factory 连接池初始化工厂
     * @param config  连接池配置
     */
    public GenericConnPool(PoolElementFactory<T> factory,
                           GenericConnPoolConfig config) {

        // 调用父类构造器
        super(config);

        // 入参校验
        if (null == factory) {
            throw new IllegalArgumentException("factory may not be null");
        }

        // 初始化阻塞队列，用于存放空闲连接
        idleElements = new LinkedBlockingDeque<PoolElement<T>>();

        this.factory = factory;

        // 拷贝配置
        setConfig(config);

        // 启动空闲连接回收线程
        startEvictor(getTimeBetweenEvictionRunsMillis());
    }

    public void setConfig(final GenericConnPoolConfig conf) {
        setLifo(conf.isLifo());
        setMaxIdle(conf.getMaxIdle());

        if (conf.getMinIdle() > conf.getMaxIdle()) {
            setMinIdle(conf.getMaxIdle());
        } else {
            setMinIdle(conf.getMinIdle());
        }

        setMaxTotal(conf.getMaxTotal());
        setMaxWaitMillis(conf.getMaxWaitMillis());
        setBlockWhenExhausted(conf.isBlockWhenExhausted());
        setTestOnCreate(conf.isTestOnCreate());
        setTestOnBorrow(conf.isTestOnBorrow());
        setTestOnReturn(conf.isTestOnReturn());
        setTestWhileIdle(conf.isTestWhileIdle());
        setNumTestsPerEvictionRun(conf.getNumTestsPerEvictionRun());
        setMinEvictableIdleTimeMillis(conf.getMinEvictableIdleTimeMillis());
        setTimeBetweenEvictionRunsMillis(
                conf.getTimeBetweenEvictionRunsMillis());
        setSoftMinEvictableIdleTimeMillis(
                conf.getSoftMinEvictableIdleTimeMillis());
        setEvictionPolicyClassName(conf.getEvictionPolicyClassName());
        setEvictorShutdownTimeoutMillis(conf.getEvictorShutdownTimeoutMillis());
    }

    /**
     * 从连接池中获取连接
     *
     * @return 连接对象
     */
    @Override
    public T borrow() {
        return borrow(getMaxWaitMillis());
    }

    /**
     * 从连接池中获取连接
     *
     * @param borrowMaxWaitMillis 如果池中没有空闲连接，则需要等待的时间
     *
     * @return 连接对象
     */
    public T borrow(long borrowMaxWaitMillis) {

        final boolean isBlockWhenExhausted = isBlockWhenExhausted();

        boolean create;
        final long waitTime = System.currentTimeMillis();

        PoolElement<T> p = null;
        while (p == null) {

            // 标识是否创建连接
            create = false;

            // 获取元素
            p = idleElements.pollFirst();

            // 如果空闲队列为空，则新建连接
            if (p == null) {
                p = createElement();
                if (p != null) {
                    create = true;
                }
            }
            if (blockWhenExhausted) {
                if (p == null) {
                    if (borrowMaxWaitMillis < 0) {
                        p = idleObjects.takeFirst();
                    } else {
                        p = idleObjects.pollFirst(borrowMaxWaitMillis,
                                TimeUnit.MILLISECONDS);
                    }
                }
                if (p == null) {
                    throw new NoSuchElementException(
                            "Timeout waiting for idle object");
                }
            } else {
                if (p == null) {
                    throw new NoSuchElementException("Pool exhausted");
                }
            }
            if (!p.allocate()) {
                p = null;
            }

            if (p != null) {
                try {
                    factory.activateObject(p);
                } catch (final Exception e) {
                    try {
                        destroy(p);
                    } catch (final Exception e1) {
                        // Ignore - activation failure is more important
                    }
                    p = null;
                    if (create) {
                        final NoSuchElementException nsee = new NoSuchElementException(
                                "Unable to activate object");
                        nsee.initCause(e);
                        throw nsee;
                    }
                }
                if (p != null && (getTestOnBorrow() || create && getTestOnCreate())) {
                    boolean validate = false;
                    Throwable validationThrowable = null;
                    try {
                        validate = factory.validateObject(p);
                    } catch (final Throwable t) {
                        PoolUtils.checkRethrow(t);
                        validationThrowable = t;
                    }
                    if (!validate) {
                        try {
                            destroy(p);
                            destroyedByBorrowValidationCount.incrementAndGet();
                        } catch (final Exception e) {
                            // Ignore - validation failure is more important
                        }
                        p = null;
                        if (create) {
                            final NoSuchElementException nsee = new NoSuchElementException(
                                    "Unable to validate object");
                            nsee.initCause(validationThrowable);
                            throw nsee;
                        }
                    }
                }
            }
        }

        updateStatsBorrow(p, System.currentTimeMillis() - waitTime);

        return p.getObject();
    }

    /**
     * 创建一个包装好的连接
     * <p>
     * 如果已经达到{@code maxTotal}的限制，则返回null；
     * </p>
     *
     * @return 包装好的连接 or null
     *
     * @throws Exception 创建连接失败，则抛出异常
     */
    private PoolElement<T> createElement() throws Exception {

        // 获取最大连接数配置
        int localMaxTotal = getMaxTotal();
        if (localMaxTotal < 0) {
            localMaxTotal = Integer.MAX_VALUE;
        }

        // Flag that indicates if create should:
        // - TRUE:  call the factory to create an object
        // - FALSE: return null
        // - null:  loop and re-test the condition that determines whether to
        //          call the factory
        Boolean create = null;
        while (create == null) {

            createLock.lock();
            try {

                final long newCreateCount = createCount.incrementAndGet();
                if (newCreateCount > localMaxTotal) {
                    // The pool is currently at capacity or in the process of
                    // making enough new objects to take it to capacity.
                    createCount.decrementAndGet();
                    if (makeObjectCount == 0) {
                        // There are no makeObject() calls in progress so the
                        // pool is at capacity. Do not attempt to create a new
                        // object. Return and wait for an object to be returned
                        create = Boolean.FALSE;
                    } else {
                        // There are makeObject() calls in progress that might
                        // bring the pool to capacity. Those calls might also
                        // fail so wait until they complete and then re-test if
                        // the pool is at capacity or not.
                        createCondition.await();
                    }
                } else {
                    // The pool is not at capacity. Create a new object.
                    makeObjectCount++;
                    create = Boolean.TRUE;
                }

            } finally {
                createLock.unlock();
            }
        }

        if (!create.booleanValue()) {
            return null;
        }

        final PoolElement<T> p;
        try {
            p = factory.create();
        } catch (final Exception e) {
            createCount.decrementAndGet();
            throw e;
        } finally {

            createLock.lock();
            try {
                makeObjectCount--;
                createCondition.notifyAll();
            } finally {
                createLock.unlock();
            }
        }

        final AbandonedConfig ac = this.abandonedConfig;
        if (ac != null && ac.getLogAbandoned()) {
            p.setLogAbandoned(true);
        }

        createdCount.incrementAndGet();
        allObjects.put(new IdentityWrapper<T>(p.getObject()), p);
        return p;
    }

    @Override
    public void returN(final T conn) {

    }

    @Override
    public void invalidate(final T conn) {

    }

    @Override
    public void create() {

    }

    @Override
    public int idleCount() {
        return 0;
    }

    @Override
    public int activeCount() {
        return 0;
    }

    @Override
    public void clear() {

    }

    @Override
    public void close() {

    }

    @Override
    void evict() throws Exception {

    }

    @Override
    void ensureMinIdle() throws Exception {

    }

    @Override
    int getNumIdle() {
        return 0;
    }

}
