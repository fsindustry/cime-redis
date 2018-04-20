package com.fsindustry.cime.redis.exception.server;

import com.fsindustry.cime.redis.exception.RedisException;

/**
 * redis LOADING类型异常
 *
 * @author fuzhengxin
 */
public class RedisLoadingException extends RedisException {

    public RedisLoadingException(String message) {
        super(message);
    }

}
