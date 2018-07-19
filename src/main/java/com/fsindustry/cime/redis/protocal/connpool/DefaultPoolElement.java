package com.fsindustry.cime.redis.protocal.connpool;

import java.util.Deque;

import com.fsindustry.cime.redis.protocal.conn.Connection;

/**
 * PooledElement的默认实现
 *
 * @author fuzhengxin
 * @date 2018/6/5
 */
public class DefaultPoolElement<T extends Connection> implements PoolElement<T> {

    /**
     * 存放包装的连接对象
     */
    private final T connection;

    /**
     * 记录元素状态
     */
    private ElementState state = ElementState.IDLE;

    /**
     * 创建时间
     */
    private final long createTime = System.currentTimeMillis();
    private volatile long lastBorrowTime = createTime;
    private volatile long lastReturnTime = createTime;

    /**
     * 记录使用次数
     */
    private volatile long borrowedCount = 0;

    public DefaultPoolElement(T connection) {
        this.connection = connection;
    }

    @Override
    public T get() {
        return connection;
    }

    @Override
    public long getCreateTime() {
        return createTime;
    }

    @Override
    public long getActiveTimeMillis() {

        // 拷贝，避免线程问题
        final long rTime = lastReturnTime;
        final long bTime = lastBorrowTime;

        // 如果对象已经归还
        if (rTime > bTime) {
            return rTime - bTime;
        }

        // 如果对象正在使用
        return System.currentTimeMillis() - bTime;
    }

    @Override
    public long getIdleTimeMillis() {
        final long elapsed = System.currentTimeMillis() - lastReturnTime;
        // elapsed may be negative if:
        // - another thread updates lastReturnTime during the calculation window
        // - System.currentTimeMillis() is not monotonic (e.g. system time is set back)
        return elapsed >= 0 ? elapsed : 0;
    }

    @Override
    public long getLastBorrowTime() {
        return lastBorrowTime;
    }

    @Override
    public long getLastReturnTime() {
        return lastReturnTime;
    }

    public long getBorrowedCount() {
        return borrowedCount;
    }

    @Override
    public int compareTo(PoolElement<T> other) {
        final long lastActiveDiff = this.getLastReturnTime() - other.getLastReturnTime();
        if (lastActiveDiff == 0) {
            // Make sure the natural ordering is broadly consistent with equals
            // although this will break down if distinct objects have the same
            // identity hash code.
            // see java.lang.Comparable Javadocs
            return System.identityHashCode(this) - System.identityHashCode(other);
        }
        // handle int overflow
        return (int) Math.min(Math.max(lastActiveDiff, Integer.MIN_VALUE), Integer.MAX_VALUE);
    }

    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder();
        result.append("Object: ");
        result.append(connection.toString());
        result.append(", State: ");
        synchronized(this) {
            result.append(state.toString());
        }
        return result.toString();
        // TODO add other attributes
    }

    @Override
    public synchronized boolean startEvictionTest() {
        if (state == ElementState.IDLE) {
            state = ElementState.EVICTION;
            return true;
        }

        return false;
    }

    @Override
    public synchronized boolean endEvictionTest(Deque idleQueue) {

        if (state == ElementState.EVICTION) {
            state = ElementState.IDLE;
            return true;
        } else if (state == ElementState.EVICTION_RETURN_TO_HEAD) {
            state = ElementState.IDLE;
            if (!idleQueue.offerFirst(this)) {
                // TODO - Should never happen
            }
        }
        return false;
    }

    @Override
    public synchronized boolean allocate() {
        if (state == ElementState.IDLE) {
            state = ElementState.ALLOCATED;
            lastBorrowTime = System.currentTimeMillis();
            borrowedCount++;
            return true;
        } else if (state == ElementState.EVICTION) {
            state = ElementState.EVICTION_RETURN_TO_HEAD;
            return false;
        }

        return false;
    }

    @Override
    public synchronized boolean deallocate() {
        if (state == ElementState.ALLOCATED ||
                state == ElementState.RETURNING) {
            state = ElementState.IDLE;
            lastReturnTime = System.currentTimeMillis();
            return true;
        }
        return false;
    }

    @Override
    public synchronized void invalidate() {
        state = ElementState.INVALID;
    }

    @Override
    public synchronized ElementState getState() {
        return state;
    }

    @Override
    public synchronized void markAbandoned() {
        state = ElementState.ABANDONED;
    }

    @Override
    public synchronized void markReturning() {
        state = ElementState.RETURNING;
    }
}
