package com.fsindustry.cime.redis.protocal.cmd;

import java.util.Set;

import com.fsindustry.cime.redis.protocal.constant.CmdEnum;
import com.fsindustry.cime.redis.protocal.constant.MsgType;
import com.fsindustry.cime.redis.protocal.parser.KeySetParser;
import com.fsindustry.cime.redis.protocal.parser.StringParser;

/**
 * 命令容器，存放创建好的命令对象
 * <p>
 * 命令对象无状态、可重用，不需要每次创建，因而启动时，创建单例放入专门的容器
 * </p>
 *
 * @author fuzhengxin
 */
public interface Cmds {

    Cmd<String> PING = new Cmd<>(CmdEnum.PING.getName(),
            CmdEnum.PING.getSubName(),
            CmdEnum.PING.getCmdType(),
            MsgType.STRING,
            new StringParser());

    Cmd<Set<Object>> KEYS = new Cmd<>(CmdEnum.KEYS.getName(),
            CmdEnum.KEYS.getSubName(),
            CmdEnum.KEYS.getCmdType(),
            MsgType.ARRAY,
            new KeySetParser());

}
