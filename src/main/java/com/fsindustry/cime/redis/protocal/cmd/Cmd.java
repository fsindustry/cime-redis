package com.fsindustry.cime.redis.protocal.cmd;

import com.fsindustry.cime.redis.protocal.constant.CmdType;
import com.fsindustry.cime.redis.protocal.constant.MsgType;
import com.fsindustry.cime.redis.protocal.parser.Parser;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * redis命令基类
 *
 * @param <Out> 返回结果类型
 *
 * @author fuzhengxin
 */
@Data
@AllArgsConstructor
public class Cmd<Out> {

    /**
     * 命令名称
     */
    private final String name;

    /**
     * 子命令名称
     */
    private final String subName;

    /**
     * 命令类型
     */
    private final CmdType cmdType;

    /**
     * 命令返回值类型
     */
    private final MsgType respType;

    /**
     * 命令解析器，用于解析命令
     */
    private final Parser respParser;
}
