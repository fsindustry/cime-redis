package com.fsindustry.cime.redis.protocal.constant;

/**
 * 错误标识
 *
 * @author fuzhengxin
 * @date 2018/4/28
 */
public interface ErrorFlag {

    String ASK = "ASK";
    String MOVED = "MOVED";
    String CLUSTERDOWN = "CLUSTERDOWN";
    String BUSY = "BUSY";
    String NOSCRIPT = "NOSCRIPT";
    String TRYAGAIN = "TRYAGAIN";
    String LOADING = "LOADING";
    String OOM = "OOM";
    String _OOM = "-OOM";
}
