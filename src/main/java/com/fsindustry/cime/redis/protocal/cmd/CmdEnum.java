package com.fsindustry.cime.redis.protocal.cmd;

import lombok.AllArgsConstructor;

/**
 * redis命令枚举
 *
 * @author fuzhengxin
 */
@AllArgsConstructor
public enum CmdEnum {

    /**
     * ping命令
     */
    PING("ping", false);

    private String name;

    private boolean hasSubCmd;
}
