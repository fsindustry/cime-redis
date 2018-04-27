package com.fsindustry.cime.redis.protocal.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * RESP协议中消息类型常量
 *
 * @author fuzhengxin
 * @date 2018/4/23
 */
@Getter
@AllArgsConstructor
public enum MsgType {

    /**
     * 简单字符串类型
     */
    STRING('+'),

    /**
     * 错误类型
     */
    ERROR('-'),

    /**
     * 数字类型
     */
    INTEGER(':'),

    /**
     * 安全字符串类型
     */
    BULK_STRING('$'),

    /**
     * 数组类型
     */
    ARRAY('*');

    /**
     * 类型在协议中的前缀
     */
    private char prefix;
}
