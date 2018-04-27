package com.fsindustry.cime.redis.protocal.constant;

import com.fsindustry.cime.redis.protocal.parser.Parser;

import io.netty.util.internal.StringUtil;
import lombok.AllArgsConstructor;

/**
 * redis命令枚举
 *
 * @author fuzhengxin
 */
@AllArgsConstructor
public enum CmdEnum {

    /**
     * 集群相关命令
     */
    CLUSTER_ADDSLOTS("cluster", "addslots", CmdType.CLUSTER, null),
    CLUSTER_COUNT_FAILURE_REPORTS("cluster", "count-failure-reports", CmdType.CLUSTER, null),
    CLUSTER_COUNTKEYSINSLOT("cluster", "countkeysinslot", CmdType.CLUSTER, null),
    CLUSTER_DELSLOTS("cluster", "delslots", CmdType.CLUSTER, null),
    CLUSTER_FAILOVER("cluster", "failover", CmdType.CLUSTER, null),
    CLUSTER_FORGET("cluster", "forget", CmdType.CLUSTER, null),
    CLUSTER_GETKEYSINSLOT("cluster", "getkeysinslot", CmdType.CLUSTER, null),
    CLUSTER_INFO("cluster", "info", CmdType.CLUSTER, null),
    CLUSTER_KEYSLOT("cluster", "keyslot", CmdType.CLUSTER, null),
    CLUSTER_MEET("cluster", "meet", CmdType.CLUSTER, null),
    CLUSTER_NODES("cluster", "nodes", CmdType.CLUSTER, null),
    CLUSTER_REPLICATE("cluster", "replicate", CmdType.CLUSTER, null),
    CLUSTER_RESET("cluster", "reset", CmdType.CLUSTER, null),
    CLUSTER_SAVECONFIG("cluster", "saveconfig", CmdType.CLUSTER, null),
    CLUSTER_SET_CONFIG_EPOCH("cluster", "set-config-epoch", CmdType.CLUSTER, null),
    CLUSTER_SETSLOT("cluster", "setslot", CmdType.CLUSTER, null),
    CLUSTER_SLAVES("cluster", "slaves", CmdType.CLUSTER, null),
    CLUSTER_SLOTS("cluster", "slots", CmdType.CLUSTER, null),
    READONLY("cluster", "readonly", CmdType.CLUSTER, null),
    READWRITE("cluster", "readwrite", CmdType.CLUSTER, null),

    /**
     * 连接相关命令
     */
    AUTH("auth", null, CmdType.CONNECTION, null),
    ECHO("echo", null, CmdType.CONNECTION, null),
    PING("ping", null, CmdType.CONNECTION, null),
    QUIT("quit", null, CmdType.CONNECTION, null),
    SELECT("select", null, CmdType.CONNECTION, null),
    SWAPDB("echo", null, CmdType.CONNECTION, null),

    /**
     * GEO相关命令
     */
    GEOADD("geoadd", null, CmdType.GEO, null),
    GEOHASH("geohash", null, CmdType.GEO, null),
    GEOPOS("geopos", null, CmdType.GEO, null),
    GEODIST("geodist", null, CmdType.GEO, null),
    GEORADIUS("georadius", null, CmdType.GEO, null),
    GEORADIUSBYMEMBER("georadiusbymember", null, CmdType.GEO, null),

    /**
     * hash相关命令
     */
    HDEL("hdel", null, CmdType.HASHES, null),
    HEXISTS("hexists", null, CmdType.HASHES, null),
    HGET("hget", null, CmdType.HASHES, null),
    HGETALL("hgetall", null, CmdType.HASHES, null),
    HINCRBY("hincrby", null, CmdType.HASHES, null),
    HINCRBYFLOAT("hincrbyfloat", null, CmdType.HASHES, null),
    HKEYS("hkeys", null, CmdType.HASHES, null),
    HLEN("hlen", null, CmdType.HASHES, null),
    HMGET("hmget", null, CmdType.HASHES, null),
    HMSET("hmset", null, CmdType.HASHES, null),
    HSCAN("hscan", null, CmdType.HASHES, null),
    HSET("hset", null, CmdType.HASHES, null),
    HSETNX("hsetnx", null, CmdType.HASHES, null),
    HSTRLEN("hstrlen", null, CmdType.HASHES, null),
    HVALS("hvals", null, CmdType.HASHES, null),

    /**
     * hyperlog相关命令
     */
    PFADD("pfadd", null, CmdType.HYPERLOGLOG, null),
    PFCOUNT("pfcount", null, CmdType.HYPERLOGLOG, null),
    PFMERGE("pfmerge", null, CmdType.HYPERLOGLOG, null),

    /**
     * key操作相关命令
     */
    DEL("del", null, CmdType.KEYS, null),
    DUMP("dump", null, CmdType.KEYS, null),
    EXISTS("exists", null, CmdType.KEYS, null),
    EXPIRE("expire", null, CmdType.KEYS, null),
    EXPIREAT("expireat", null, CmdType.KEYS, null),
    KEYS("keys", null, CmdType.KEYS, null),
    MIGRATE("migrate", null, CmdType.KEYS, null),
    MOVE("move", null, CmdType.KEYS, null),
    OBJECT("object", null, CmdType.KEYS, null),
    PERSIST("persist", null, CmdType.KEYS, null),
    PEXPIRE("pexpire", null, CmdType.KEYS, null),
    PEXPIREAT("pexpireat", null, CmdType.KEYS, null),
    PTTL("pttl", null, CmdType.KEYS, null),
    RANDOMKEY("randomkey", null, CmdType.KEYS, null),
    RENAME("rename", null, CmdType.KEYS, null),
    RENAMENX("renamenx", null, CmdType.KEYS, null),
    RESTORE("restore", null, CmdType.KEYS, null),
    SCAN("scan", null, CmdType.KEYS, null),
    SORT("sort", null, CmdType.KEYS, null),
    TOUCH("touch", null, CmdType.KEYS, null),
    TTL("ttl", null, CmdType.KEYS, null),
    TYPE("type", null, CmdType.KEYS, null),
    UNLINK("unlink", null, CmdType.KEYS, null),
    WAIT("wait", null, CmdType.KEYS, null),

    /**
     * list操作相关命令
     */
    BLPOP("blpop", null, CmdType.LISTS, null),
    BRPOP("brpop", null, CmdType.LISTS, null),
    BRPOPLPUSH("brpoplpush", null, CmdType.LISTS, null),
    LINDEX("lindex", null, CmdType.LISTS, null),
    LINSERT("linsert", null, CmdType.LISTS, null),
    LLEN("llen", null, CmdType.LISTS, null),
    LPOP("lpop", null, CmdType.LISTS, null),
    LPUSH("lpush", null, CmdType.LISTS, null),
    LPUSHX("lpushx", null, CmdType.LISTS, null),
    LRANGE("lrange", null, CmdType.LISTS, null),
    LREM("lrem", null, CmdType.LISTS, null),
    LSET("lset", null, CmdType.LISTS, null),
    LTRIM("ltrim", null, CmdType.LISTS, null),
    RPOP("rpop", null, CmdType.LISTS, null),
    RPOPLPUSH("rpoplpush", null, CmdType.LISTS, null),
    RPUSH("rpush", null, CmdType.LISTS, null),
    RPUSHX("rpushx", null, CmdType.LISTS, null),

    /**
     * pub/sub相关命令
     */
    PSUBSCRIBE("psubscribe", null, CmdType.PUB_SUB, null),
    PUBLISH("publish", null, CmdType.PUB_SUB, null),
    PUBSUB("pubsub", null, CmdType.PUB_SUB, null),
    PUNSUBSCRIBE("punsubscribe", null, CmdType.PUB_SUB, null),
    SUBSCRIBE("subscribe", null, CmdType.PUB_SUB, null),
    UNSUBSCRIBE("unsubscribe", null, CmdType.PUB_SUB, null),

    /**
     * 脚本执行
     */
    EVAL("eval", null, CmdType.SCRIPTING, null),
    EVALSHA("evalsha", null, CmdType.SCRIPTING, null),
    SCRIPT_DEBUG("script", "debug", CmdType.SCRIPTING, null),
    SCRIPT_EXISTS("script", "exists", CmdType.SCRIPTING, null),
    SCRIPT_FLUSH("script", "flush", CmdType.SCRIPTING, null),
    SCRIPT_KILL("script", "kill", CmdType.SCRIPTING, null),
    SCRIPT_LOAD("script", "load", CmdType.SCRIPTING, null),

    /**
     * 服务器管理相关命令
     */
    BGREWRITEAOF("BGREWRITEAOF", null, CmdType.SERVER, null),
    BGSAVE("BGSAVE", null, CmdType.SERVER, null),
    CLIENT_GETNAME("CLIENT", "GETNAME", CmdType.SERVER, null),
    CLIENT_KILL("CLIENT", "KILL", CmdType.SERVER, null),
    CLIENT_LIST("CLIENT", "LIST", CmdType.SERVER, null),
    CLIENT_PAUSE("CLIENT", "PAUSE", CmdType.SERVER, null),
    CLIENT_REPLY("CLIENT", "REPLY", CmdType.SERVER, null),
    CLIENT_SETNAME("CLIENT", "SETNAME", CmdType.SERVER, null),
    COMMAND("COMMAND", null, CmdType.SERVER, null),
    COMMAND_COUNT("COMMAND", "COUNT", CmdType.SERVER, null),
    COMMAND_GETKEYS("COMMAND", "GETKEYS", CmdType.SERVER, null),
    COMMAND_INFO("COMMAND", "INFO", CmdType.SERVER, null),
    CONFIG_GET("CONFIG", "GET", CmdType.SERVER, null),
    CONFIG_RESETSTAT("CONFIG", "RESETSTAT", CmdType.SERVER, null),
    CONFIG_REWRITE("CONFIG", "REWRITE", CmdType.SERVER, null),
    CONFIG_SET("CONFIG", "SET", CmdType.SERVER, null),
    DBSIZE("DBSIZE", "load", CmdType.SERVER, null),
    DEBUG_OBJECT("DEBUG", "OBJECT", CmdType.SERVER, null),
    DEBUG_SEGFAULT("DEBUG", "SEGFAULT", CmdType.SERVER, null),
    FLUSHALL("FLUSHALL", "load", CmdType.SERVER, null),
    FLUSHDB("FLUSHDB", "load", CmdType.SERVER, null),
    INFO("INFO", "load", CmdType.SERVER, null),
    LASTSAVE("LASTSAVE", "load", CmdType.SERVER, null),
    MEMORY_DOCTOR("MEMORY", "DOCTOR", CmdType.SERVER, null),
    MEMORY_HELP("MEMORY", "HELP", CmdType.SERVER, null),
    MEMORY_MALLOC_STATS("MEMORY", "MALLOC-STATS", CmdType.SERVER, null),
    MEMORY_PURGE("MEMORY", "PURGE", CmdType.SERVER, null),
    MEMORY_STATS("MEMORY", "STATS", CmdType.SERVER, null),
    MEMORY_USAGE("MEMORY", "USAGE", CmdType.SERVER, null),
    MONITOR("MONITOR", null, CmdType.SERVER, null),
    ROLE("ROLE", null, CmdType.SERVER, null),
    SAVE("SAVE", null, CmdType.SERVER, null),
    SHUTDOWN("SHUTDOWN", null, CmdType.SERVER, null),
    SLAVEOF("SLAVEOF", null, CmdType.SERVER, null),
    SLOWLOG("SLOWLOG", null, CmdType.SERVER, null),
    SYNC("SYNC", null, CmdType.SERVER, null),
    TIME("TIME", null, CmdType.SERVER, null),

    /**
     * set相关操作
     */
    SADD("SADD", null, CmdType.SETS, null),
    SCARD("SCARD", null, CmdType.SETS, null),
    SDIFF("SDIFF", null, CmdType.SETS, null),
    SDIFFSTORE("SDIFFSTORE", null, CmdType.SETS, null),
    SINTER("SINTER", null, CmdType.SETS, null),
    SINTERSTORE("SINTERSTORE", null, CmdType.SETS, null),
    SISMEMBER("SISMEMBER", null, CmdType.SETS, null),
    SMEMBERS("SMEMBERS", null, CmdType.SETS, null),
    SMOVE("SMOVE", null, CmdType.SETS, null),
    SPOP("SPOP", null, CmdType.SETS, null),
    SRANDMEMBER("SRANDMEMBER", null, CmdType.SETS, null),
    SREM("SREM", null, CmdType.SETS, null),
    SSCAN("SSCAN", null, CmdType.SETS, null),
    SUNION("SUNION", null, CmdType.SETS, null),
    SUNIONSTORE("SUNIONSTORE", null, CmdType.SETS, null),

    /**
     * 有序集操作命令
     */
    ZADD("ZADD", null, CmdType.SORTED_SETS, null),
    ZCARD("ZCARD", null, CmdType.SORTED_SETS, null),
    ZCOUNT("ZCOUNT", null, CmdType.SORTED_SETS, null),
    ZINCRBY("ZINCRBY", null, CmdType.SORTED_SETS, null),
    ZINTERSTORE("ZINTERSTORE", null, CmdType.SORTED_SETS, null),
    ZLEXCOUNT("ZLEXCOUNT", null, CmdType.SORTED_SETS, null),
    ZRANGE("ZRANGE", null, CmdType.SORTED_SETS, null),
    ZRANGEBYLEX("ZRANGEBYLEX", null, CmdType.SORTED_SETS, null),
    ZRANGEBYSCORE("ZRANGEBYSCORE", null, CmdType.SORTED_SETS, null),
    ZRANK("ZRANK", null, CmdType.SORTED_SETS, null),
    ZREM("ZREM", null, CmdType.SORTED_SETS, null),
    ZREMRANGEBYLEX("ZREMRANGEBYLEX", null, CmdType.SORTED_SETS, null),
    ZREMRANGEBYRANK("ZREMRANGEBYRANK", null, CmdType.SORTED_SETS, null),
    ZREMRANGEBYSCORE("ZREMRANGEBYSCORE", null, CmdType.SORTED_SETS, null),
    ZREVRANGE("ZREVRANGE", null, CmdType.SORTED_SETS, null),
    ZREVRANGEBYLEX("ZREVRANGEBYLEX", null, CmdType.SORTED_SETS, null),
    ZREVRANGEBYSCORE("ZREVRANGEBYSCORE", null, CmdType.SORTED_SETS, null),
    ZREVRANK("ZREVRANK", null, CmdType.SORTED_SETS, null),
    ZSCAN("ZSCAN", null, CmdType.SORTED_SETS, null),
    ZSCORE("ZSCORE", null, CmdType.SORTED_SETS, null),
    ZUNIONSTORE("ZUNIONSTORE", null, CmdType.SORTED_SETS, null),

    /**
     * string相关操作
     */
    APPEND("APPEND", null, CmdType.STRINGS, null),
    BITCOUNT("BITCOUNT", null, CmdType.STRINGS, null),
    BITFIELD("BITFIELD", null, CmdType.STRINGS, null),
    BITOP("BITOP", null, CmdType.STRINGS, null),
    BITPOS("BITPOS", null, CmdType.STRINGS, null),
    DECR("DECR", null, CmdType.STRINGS, null),
    DECRBY("DECRBY", null, CmdType.STRINGS, null),
    GET("GET", null, CmdType.STRINGS, null),
    GETBIT("GETBIT", null, CmdType.STRINGS, null),
    GETRANGE("GETRANGE", null, CmdType.STRINGS, null),
    GETSET("GETSET", null, CmdType.STRINGS, null),
    INCR("INCR", null, CmdType.STRINGS, null),
    INCRBY("INCRBY", null, CmdType.STRINGS, null),
    INCRBYFLOAT("INCRBYFLOAT", null, CmdType.STRINGS, null),
    MGET("MGET", null, CmdType.STRINGS, null),
    MSET("MSET", null, CmdType.STRINGS, null),
    MSETNX("MSETNX", null, CmdType.STRINGS, null),
    PSETEX("PSETEX", null, CmdType.STRINGS, null),
    SET("SET", null, CmdType.STRINGS, null),
    SETBIT("SETBIT", null, CmdType.STRINGS, null),
    SETEX("SETEX", null, CmdType.STRINGS, null),
    SETNX("SETNX", null, CmdType.STRINGS, null),
    SETRANGE("SETRANGE", null, CmdType.STRINGS, null),
    STRLEN("STRLEN", null, CmdType.STRINGS, null),

    /**
     * 事务相关命令
     */
    DISCARD("DISCARD", null, CmdType.TRANSACTIONS, null),
    EXEC("EXEC", null, CmdType.TRANSACTIONS, null),
    MULTI("MULTI", null, CmdType.TRANSACTIONS, null),
    UNWATCH("UNWATCH", null, CmdType.TRANSACTIONS, null),
    WATCH("WATCH", null, CmdType.TRANSACTIONS, null);

    private String name;

    private String subName;

    private CmdType cmdType;

    private Class<? extends Parser> converterCls;

    public boolean hasSubCmd() {
        return !StringUtil.isNullOrEmpty(subName);
    }
}
