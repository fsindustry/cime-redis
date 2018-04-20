package com.fsindustry.cime.redis.protocal.req;

/**
 * 请求数据接口
 *
 * @author fuzhengxin
 */
public interface Req {

    /**
     * 捕获异常后通知上游
     *
     * @param cause 请求异常
     *
     * @return true，通知成功；
     */
    boolean tryFailure(Throwable cause);
}
