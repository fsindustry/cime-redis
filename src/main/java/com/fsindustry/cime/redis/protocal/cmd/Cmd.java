package com.fsindustry.cime.redis.protocal.cmd;

import lombok.Data;

/**
 * redis命令基类
 *
 * @param <Out> 返回结果类型
 *
 * @author fuzhengxin
 */
@Data
public class Cmd<Out> {

    private final String name;

    private final String subName;

}
