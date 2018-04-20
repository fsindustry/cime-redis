package com.fsindustry.cime.redis.config;

import java.util.List;

import com.fsindustry.cime.redis.config.constant.DeployMode;

import lombok.Getter;
import lombok.Setter;

/**
 * redis客户端配置基类
 *
 * @author fuzhengxin
 */
@Getter
@Setter
public class Config {

    private DeployMode deployMode;

    private List<String> address;

    private int nettyThreads;

    private String password;

    private int database;

    private String clientName;

    private boolean readOnly;

    private long cmdTimeout;

    /**
     * 判断redis是否需要认证
     *
     * @return true，需要认证；false，不需要认证
     */
    public boolean needPassword() {
        return null != password && !"".equals(password.trim());
    }

}
