package com.fsindustry.cime.redis.protocal.cmd;

import java.util.Set;

import com.fsindustry.cime.redis.protocal.constant.CmdEnum;
import com.fsindustry.cime.redis.protocal.constant.MsgType;
import com.fsindustry.cime.redis.protocal.parser.KeySetParser;
import com.fsindustry.cime.redis.protocal.parser.LongParser;
import com.fsindustry.cime.redis.protocal.parser.StringParser;

/**
 * 存放创建好的命令对象，便于重用
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

    Cmd<Long> DBSIZE = new Cmd<>(CmdEnum.DBSIZE.getName(),
            CmdEnum.DBSIZE.getSubName(),
            CmdEnum.DBSIZE.getCmdType(),
            MsgType.INTEGER,
            new LongParser());
}
