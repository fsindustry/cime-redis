package com.fsindustry.cime.redis.protocal.constant;

/**
 * 标识命令操作类型
 *
 * @author fuzhengxin
 * @date 2018/4/24
 */
public enum CmdType {

    /**
     * 集群操作相关命令
     */
    CLUSTER,

    /**
     * 连接相关命令
     */
    CONNECTION,

    /**
     * 地理位置相关命令
     */
    GEO,

    /**
     * hash相关命令
     */
    HASHES,

    /**
     * hyperlog相关命令
     */
    HYPERLOGLOG,

    /**
     * key操作相关命令
     */
    KEYS,

    /**
     * 列表操作命令
     */
    LISTS,

    /**
     * 订阅/发布相关命令
     */
    PUB_SUB,

    /**
     * 脚本命令
     */
    SCRIPTING,

    /**
     * 服务器端命令
     */
    SERVER,

    /**
     * 无序集相关命令
     */
    SETS,

    /**
     * 有序集相关命令
     */
    SORTED_SETS,

    /**
     * 字符串相关命令
     */
    STRINGS,

    /**
     * 事务相关命令
     */
    TRANSACTIONS;

}
