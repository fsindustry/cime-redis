package com.fsindustry.cime.redis.model;

import java.util.List;

/**
 * 集群：表示由多个分片组成的一个集群
 *
 * @author fuzhengxin
 */
public class Cluster implements Operator {

    private List<Shard> shards;
}
