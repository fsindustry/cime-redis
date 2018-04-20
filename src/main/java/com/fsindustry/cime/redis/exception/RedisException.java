package com.fsindustry.cime.redis.exception;

/**
 * redis异常基类
 * @author fuzhengxin
 */
public class RedisException extends RuntimeException {

    public RedisException() {
    }

    public RedisException(String message) {
        super(message);
    }

    public RedisException(String message, Throwable cause) {
        super(message, cause);
    }

}
