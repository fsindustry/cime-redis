package com.fsindustry.cime.redis.config;

import com.fsindustry.cime.redis.config.constant.DeployMode;

/**
 * 配置工厂
 * <p>
 * 基于简单工厂模式，根据redis部署模式创建响应的配置
 *
 * @author fuzhengxin
 */
public class ConfigParser {

    public Config get(DeployMode mode) {

        if (DeployMode.CLUSTER.equals(mode)) {
            return new ClusterModeConfig();
        } else if (DeployMode.SENTINEL.equals(mode)) {
            return new SentinelModeConfig();
        } else if (DeployMode.MASTER_SLAVE.equals(mode)) {
            return new MasterSlaveModeConfig();
        } else {
            // TODO 抛出异常
        }

        return null;
    }

}
