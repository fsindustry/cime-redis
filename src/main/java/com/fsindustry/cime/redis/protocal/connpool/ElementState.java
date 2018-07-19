package com.fsindustry.cime.redis.protocal.connpool;

/**
 * 连接池中连接的状态
 *
 * @author fuzhengxin
 * @date 2018/6/5
 */
public enum ElementState {

    /**
     * 空闲状态，处于队列中
     */
    IDLE,

    /**
     * 使用中
     */
    ALLOCATED,

    /**
     * 正在被检查是否要回收，处于队列中
     */
    EVICTION,

    /**
     * Not in the queue, currently being tested for possible eviction. An
     * attempt to borrow the object was made while being tested which removed it
     * from the queue. It should be returned to the head of the queue once
     * eviction testing completes.
     * TODO: Consider allocating object and ignoring the result of the eviction
     * test.
     */
    /**
     * 不在队列中，
     */
    EVICTION_RETURN_TO_HEAD,

    /**
     * 正在被校验中，处于队列中
     */
    VALIDATION,

    /**
     * Not in queue, currently being validated. The object was borrowed while
     * being validated and since testOnBorrow was configured, it was removed
     * from the queue and pre-allocated. It should be allocated once validation
     * completes.
     */
    VALIDATION_PREALLOCATED,

    /**
     * Not in queue, currently being validated. An attempt to borrow the object
     * was made while previously being tested for eviction which removed it from
     * the queue. It should be returned to the head of the queue once validation
     * completes.
     */
    VALIDATION_RETURN_TO_HEAD,

    /**
     * 连接维护失败，将要被销毁或者已经销毁
     */
    INVALID,

    /**
     * Deemed abandoned, to be invalidated.
     */
    ABANDONED,

    /**
     * 归还连接到连接池
     */
    RETURNING
}
