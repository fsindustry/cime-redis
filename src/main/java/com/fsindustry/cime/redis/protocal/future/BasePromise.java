package com.fsindustry.cime.redis.protocal.future;

import java.lang.reflect.Field;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.ImmediateEventExecutor;
import io.netty.util.concurrent.Promise;

/**
 * promise基类
 *
 * @param <Out> 返回值类型
 *
 * @author fuzhengxin
 */
public class BasePromise<Out> implements PromiseExt<Out> {

    private static final Field LISTENERS_FIELD;

    static {
        try {
            LISTENERS_FIELD = DefaultPromise.class.getDeclaredField("listeners");
            LISTENERS_FIELD.setAccessible(true);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * 包装一个内部的promise，具体动作均委托promise进行
     */
    private final Promise<Out> promise = ImmediateEventExecutor.INSTANCE.newPromise();

    /**
     * 快速创建一个抛出异常的Future对象
     *
     * @param cause 抛出异常
     * @param <V>   输出类型
     *
     * @return 具体的future对象
     */
    public static <V> Future<V> newFailedFuture(Throwable cause) {
        BasePromise<V> future = new BasePromise<V>();
        future.tryFailure(cause);
        return future;
    }

    /**
     * 快速创建一个成功的Future对象
     *
     * @param result 输出结果
     * @param <V>    输出类型
     *
     * @return 具体的future对象
     */
    public static <V> Future<V> newSucceededFuture(V result) {
        BasePromise<V> future = new BasePromise<V>();
        future.trySuccess(result);
        return future;
    }

    @Override
    public Promise<Out> setSuccess(Out result) {
        return promise.setSuccess(result);
    }

    @Override
    public boolean trySuccess(Out result) {
        return promise.trySuccess(result);
    }

    @Override
    public Promise<Out> setFailure(Throwable cause) {
        return promise.setFailure(cause);
    }

    @Override
    public boolean tryFailure(Throwable cause) {
        return promise.tryFailure(cause);
    }

    @Override
    public boolean setUncancellable() {
        return promise.setUncancellable();
    }

    @Override
    public boolean isSuccess() {
        return promise.isSuccess();
    }

    @Override
    public boolean isCancellable() {
        return promise.isCancellable();
    }

    @Override
    public Throwable cause() {
        return promise.cause();
    }

    @Override
    public Promise<Out> addListener(GenericFutureListener<? extends Future<? super Out>> listener) {
        return promise.addListener(listener);
    }

    @Override
    public Promise<Out> addListeners(GenericFutureListener<? extends Future<? super Out>>[] listeners) {
        return promise.addListeners(listeners);
    }

    @Override
    public Promise<Out> removeListener(GenericFutureListener<? extends Future<? super Out>> listener) {
        return promise.removeListener(listener);
    }

    @Override
    public Promise<Out> removeListeners(GenericFutureListener<? extends Future<? super Out>>[] listeners) {
        return promise.removeListeners(listeners);
    }

    @Override
    public Promise<Out> await() throws InterruptedException {
        promise.await();
        return this;
    }

    @Override
    public Promise<Out> awaitUninterruptibly() {
        promise.awaitUninterruptibly();
        return this;
    }

    @Override
    public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
        return promise.await(timeout, unit);
    }

    @Override
    public boolean await(long timeoutMillis) throws InterruptedException {
        return promise.await(timeoutMillis);
    }

    @Override
    public boolean awaitUninterruptibly(long timeout, TimeUnit unit) {
        return promise.awaitUninterruptibly(timeout, unit);
    }

    @Override
    public boolean awaitUninterruptibly(long timeoutMillis) {
        return promise.awaitUninterruptibly(timeoutMillis);
    }

    @Override
    public Out getNow() {
        return promise.getNow();
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return promise.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
        return promise.isCancelled();
    }

    @Override
    public boolean isDone() {
        return promise.isDone();
    }

    @Override
    public Out get() throws InterruptedException, ExecutionException {
        return promise.get();
    }

    @Override
    public Out get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return promise.get(timeout, unit);
    }

    @Override
    public Promise<Out> sync() throws InterruptedException {
        promise.sync();
        return this;
    }

    @Override
    public Promise<Out> syncUninterruptibly() {
        promise.syncUninterruptibly();
        return this;
    }

    @Override
    public boolean hasListeners() {
        try {
            return LISTENERS_FIELD.get(promise) != null;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
