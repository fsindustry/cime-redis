package com.fsindustry.cime.redis.protocal.future;

import io.netty.util.concurrent.Promise;

/**
 * Promise扩展接口
 *
 * @author fuzhengxin
 */
public interface PromiseExt<Out> extends Promise<Out> {

    /**
     * 是否有监听器
     *
     * @return true，有；false，没有；
     */
    boolean hasListeners();
}
