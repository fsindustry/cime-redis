package com.fsindustry.cime.redis.protocal.connpool;

import java.io.Closeable;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.ref.WeakReference;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Deque;
import java.util.Iterator;
import java.util.TimerTask;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.fsindustry.cime.redis.protocal.conn.Connection;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 连接池基类
 *
 * @author fuzhengxin
 * @date 2018/5/8
 */
@ToString
public abstract class BaseConnPool<T extends Connection>
        implements Closeable, ConnectionPool<T> {

    /*------------------------连接池配置属性--------------------------*/

    /**
     * 连接池允许的最大连接数
     */
    @Getter
    @Setter
    private volatile int maxTotal = BaseConnPoolConfig.DEFAULT_MAX_TOTAL;

    /**
     * 当连接耗尽时，是否阻塞调用线程
     */
    @Getter
    @Setter
    private volatile boolean blockWhenExhausted = BaseConnPoolConfig.DEFAULT_BLOCK_WHEN_EXHAUSTED;

    /**
     * 连接耗尽时，阻塞等待空闲连接的最大时长
     */
    @Getter
    @Setter
    private volatile long maxWaitMillis = BaseConnPoolConfig.DEFAULT_MAX_WAIT_MILLIS;

    /**
     * 是否使用先进先出策略分配连接
     */
    @Getter
    @Setter
    private volatile boolean lifo = BaseConnPoolConfig.DEFAULT_LIFO;

    /**
     * 是否采用公平策略分配连接
     */
    @Getter
    private final boolean fairness;

    /**
     * 当创建新的连接后，是否检测连接是否正常
     */
    @Getter
    @Setter
    private volatile boolean testOnCreate = BaseConnPoolConfig.DEFAULT_TEST_ON_CREATE;

    /**
     * 当获取连接时，是否检测连接是否正常
     */
    @Getter
    @Setter
    private volatile boolean testOnBorrow = BaseConnPoolConfig.DEFAULT_TEST_ON_BORROW;

    /**
     * 当归还连接时，是否检测连接是否正常
     */
    @Getter
    @Setter
    private volatile boolean testOnReturn = BaseConnPoolConfig.DEFAULT_TEST_ON_RETURN;

    /**
     * 当连接空闲时，是否检测连接是否正常
     */
    @Getter
    @Setter
    private volatile boolean testWhileIdle = BaseConnPoolConfig.DEFAULT_TEST_WHILE_IDLE;

    /**
     * 回收检查时间间隔
     */
    @Getter
    @Setter
    private volatile long timeBetweenEvictionRunsMillis =
            BaseConnPoolConfig.DEFAULT_TIME_BETWEEN_EVICTION_RUNS_MILLIS;

    /**
     * 每次回收检查元素个数
     */
    @Getter
    @Setter
    private volatile int numTestsPerEvictionRun = BaseConnPoolConfig.DEFAULT_NUM_TESTS_PER_EVICTION_RUN;

    @Getter
    @Setter
    private volatile long minEvictableIdleTimeMillis = BaseConnPoolConfig.DEFAULT_MIN_EVICTABLE_IDLE_TIME_MILLIS;

    @Getter
    @Setter
    private volatile long softMinEvictableIdleTimeMillis =
            BaseConnPoolConfig.DEFAULT_SOFT_MIN_EVICTABLE_IDLE_TIME_MILLIS;

    /**
     * 回收检查策略
     */
    @Getter
    private volatile EvictionPolicy<T> evictionPolicy;

    /**
     * 回收检查器关闭超时时间
     */
    @Getter
    @Setter
    private volatile long evictorShutdownTimeoutMillis =
            BaseConnPoolConfig.DEFAULT_EVICTOR_SHUTDOWN_TIMEOUT_MILLIS;

    /**
     * 连接池内部基础状态属性
     */
    final Object closeLock = new Object();

    @Getter
    volatile boolean closed = false;

    /**
     * 控制回收过程的锁
     */
    final Lock evictionLock = new ReentrantLock();

    /**
     * 回收检查器实现
     *
     * @GuardedBy("evictionLock")
     */
    private Evictor evictor = null;

    /**
     * @GuardedBy("evictionLock")
     */
    EvictionIterator evictionIterator = null;

    /**
     * 用于存放当前连接池对应的类加载器
     */
    private final WeakReference<ClassLoader> factoryClassLoader;

    @Getter
    private volatile SwallowedExceptionListener swallowedExceptionListener = null;

    /**
     * 初始化连接池
     *
     * @param config 连接池配置
     */
    BaseConnPool(BaseConnPoolConfig config) {

        // 保存创建连接池的类加载器，便于后面回收空闲连接时使用
        final ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) {
            this.factoryClassLoader = null;
        } else {
            this.factoryClassLoader = new WeakReference<>(cl);
        }

        this.fairness = config.isFairness();
    }

    /**
     * 关闭连接池，取消JMX监控
     */
    @Override
    public abstract void close();

    /**
     * 执行空闲连接检查
     *
     * @throws Exception 检查异常
     */
    abstract void evict() throws Exception;

    /**
     * 保持最小可用连接
     *
     * @throws Exception 创建连接异常
     */
    abstract void ensureMinIdle() throws Exception;

    /**
     * 查看当前池中空闲连接
     *
     * @return 空闲连接数
     */
    abstract int getNumIdle();

    /**
     * 校验连接池是否开启
     *
     * @throws IllegalStateException 如果已经关闭，则抛出异常
     */
    final void assertOpen() throws IllegalStateException {
        if (isClosed()) {
            throw new IllegalStateException("Pool not open");
        }
    }

    /**
     * 启动回收器线程，如果已经在运行了，则停止旧的回收器，重新创建并运行；
     *
     * @param intervalMillis 运行间隔（毫秒）
     */
    final void startEvictor(final long intervalMillis) {
        evictionLock.lock();
        try {
            if (null != evictor) {
                EvictionTimer.cancel(evictor, evictorShutdownTimeoutMillis, TimeUnit.MILLISECONDS);
                evictor = null;
                evictionIterator = null;
            }
            if (intervalMillis > 0) {
                evictor = new Evictor();
                EvictionTimer.schedule(evictor, intervalMillis, intervalMillis);
            }
        } finally {
            evictionLock.unlock();
        }
    }

    /**
     * 设置回收策略类名
     *
     * @param evictionPolicyClassName 回收策略类名
     */
    final void setEvictionPolicyClassName(final String evictionPolicyClassName) {
        try {
            Class<?> clazz;
            try {
                clazz = Class.forName(evictionPolicyClassName, true,
                        Thread.currentThread().getContextClassLoader());
            } catch (final ClassNotFoundException e) {
                clazz = Class.forName(evictionPolicyClassName);
            }
            final Object policy = clazz.newInstance();
            if (policy instanceof EvictionPolicy<?>) {
                @SuppressWarnings("unchecked") // safe, because we just checked the class
                final EvictionPolicy<T> evicPolicy = (EvictionPolicy<T>) policy;
                this.evictionPolicy = evicPolicy;
            } else {
                throw new IllegalArgumentException("[" + evictionPolicyClassName +
                        "] does not implement EvictionPolicy");
            }
        } catch (final ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new IllegalArgumentException(
                    "Unable to create EvictionPolicy instance of type " +
                            evictionPolicyClassName, e);
        }
    }

    /**
     * 获取异常堆栈字符串
     *
     * @param e 异常对象
     *
     * @return 异常堆栈字符串
     */
    private String getStackTrace(final Exception e) {
        // 保留异常对象本身，可能导致异常对象一直驻留内存，造成内存溢出；
        final Writer w = new StringWriter();
        final PrintWriter pw = new PrintWriter(w);
        e.printStackTrace(pw);
        return w.toString();
    }

    /**
     * 捕获异常信息并通知监听器处理
     *
     * @param e 需要被捕获的异常
     */
    final void swallowException(final Exception e) {
        final SwallowedExceptionListener listener = getSwallowedExceptionListener();

        if (listener == null) {
            return;
        }

        try {
            listener.onSwallowException(e);
        } catch (final VirtualMachineError vme) {
            throw vme;
        } catch (final Throwable t) {
            // Ignore. Enjoy the irony.
        }
    }

    /**
     * 用来回收多余连接的回收器
     */
    class Evictor extends TimerTask {

        @Override
        public void run() {

            // 保存执行线程现有的类加载器
            final ClassLoader savedClassLoader = Thread.currentThread().getContextClassLoader();

            try {

                // 获取连接池自己的类加载器
                if (null != factoryClassLoader) {
                    final ClassLoader cl = factoryClassLoader.get();

                    // 如果类加载器已经被回收，则不作任何操作
                    if (null == cl) {
                        return;
                    }

                    Thread.currentThread().setContextClassLoader(cl);
                }

                // 回收多余的线程
                try {
                    evict();
                } catch (final Exception e) {
                    swallowException(e);
                } catch (final OutOfMemoryError oome) {
                    // Log problem but give evictor thread a chance to continue
                    // in case error is recoverable
                    oome.printStackTrace(System.err);
                }

                // 维持最小连接数
                try {
                    ensureMinIdle();
                } catch (final Exception e) {
                    swallowException(e);
                }

            } finally {

                // 恢复线程的类加载器
                Thread.currentThread().setContextClassLoader(savedClassLoader);
            }
        }
    }

    /**
     * 存放空闲线程的迭代器
     */
    class EvictionIterator implements Iterator<PoolElement<T>> {

        private final Deque<PoolElement<T>> idleObjects;
        private final Iterator<PoolElement<T>> idleElementIterator;

        /**
         * 根据传入双向队列初始化迭代器
         *
         * @param idleElements 双端队列
         */
        EvictionIterator(final Deque<PoolElement<T>> idleElements) {
            this.idleObjects = idleElements;

            if (isLifo()) {
                idleElementIterator = idleElements.descendingIterator();
            } else {
                idleElementIterator = idleElements.iterator();
            }
        }

        /**
         * 返回双向队列引用
         *
         * @return 迭代器包含的双向队列的引用
         */
        public Deque<PoolElement<T>> getIdleElements() {
            return idleObjects;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean hasNext() {
            return idleElementIterator.hasNext();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public PoolElement<T> next() {
            return idleElementIterator.next();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void remove() {
            idleElementIterator.remove();
        }

    }

    /**
     * 回收线程调度器
     */
    @ToString
    static class EvictionTimer {

        /**
         * 保存调度器引用
         */
        private static ScheduledThreadPoolExecutor executor;

        /**
         * 记录等待调度的任务数
         */
        private static int usageCount;

        /**
         * 私有化构造器，不能初始化实例
         */
        private EvictionTimer() {
        }

        /**
         * 调度回收任务
         *
         * @param task   被调度的任务
         * @param delay  调度延迟（毫秒）
         * @param period 调度周期（毫秒）
         */
        static synchronized void schedule(final Runnable task, final long delay, final long period) {
            if (null == executor) {
                executor = new ScheduledThreadPoolExecutor(1, new EvictorThreadFactory());
            }
            usageCount++;
            executor.scheduleWithFixedDelay(task, delay, period, TimeUnit.MILLISECONDS);
        }

        /**
         * 取消回收任务
         *
         * @param task    被取消的任务
         * @param timeout 等待executor终止的超时时间
         * @param unit    超时时间的单位
         */
        static synchronized void cancel(final TimerTask task, final long timeout, final TimeUnit unit) {
            task.cancel();
            usageCount--;
            if (usageCount == 0) {
                executor.shutdown();
                try {
                    executor.awaitTermination(timeout, unit);
                } catch (final InterruptedException e) {
                    // Swallow
                    // Significant API changes would be required to propagate this
                }
                executor.setCorePoolSize(0);
                executor = null;
            }
        }

        /**
         * 回收线程ThreadFactory类
         */
        private static class EvictorThreadFactory implements ThreadFactory {

            @Override
            public Thread newThread(final Runnable r) {
                final Thread t = new Thread(null, r, "connection-pool-evictor-thread");

                // 设置线程的类加载器同EvictorThreadFactory的类加载器相同
                AccessController.doPrivileged((PrivilegedAction<Void>) () -> {
                    t.setContextClassLoader(EvictorThreadFactory.class.getClassLoader());
                    return null;
                });

                return t;
            }
        }
    }
}
