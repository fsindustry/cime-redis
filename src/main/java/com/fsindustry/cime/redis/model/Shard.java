package com.fsindustry.cime.redis.model;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

/**
 * 分片：包含一主多从的redis实例
 *
 * @author fuzhengxin
 */
@Slf4j
public class Shard implements Operator {

    private List<Redis> redis;
}
