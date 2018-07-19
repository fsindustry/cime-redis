package com.fsindustry.cime.redis.protocal.connpool;

/**
 * @author fuzhengxin
 * @date 2018/6/6
 */
public interface SwallowedExceptionListener {

    /**
     * This method is called every time the implementation unavoidably swallows
     * an exception.
     *
     * @param e The exception that was swallowed
     */
    void onSwallowException(Exception e);
}
