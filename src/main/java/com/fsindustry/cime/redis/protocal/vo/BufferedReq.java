package com.fsindustry.cime.redis.protocal.vo;

/**
 * 请求数据接口
 * <p>
 * 允许被缓存
 *
 * @author fuzhengxin
 */
public interface BufferedReq {

    /**
     * 捕获异常后通知上游
     *
     * @param cause 请求异常
     *
     * @return true，通知成功；
     */
    boolean tryFailure(Throwable cause);

    /**
     * 成功后设置结果
     *
     * @param result 成功后的结果
     *
     * @return true，通知成功；
     */
    boolean trySuccess(Object result);

    /**
     * 返回异常信息，没有则返回null
     *
     * @return 异常信息
     */
    Throwable cause();

    /**
     * 判断是否成功
     *
     * @return true，成功；
     */
    boolean isSuccess();
}
