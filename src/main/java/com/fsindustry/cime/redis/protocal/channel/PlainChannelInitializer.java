package com.fsindustry.cime.redis.protocal.channel;

import com.fsindustry.cime.redis.protocal.handler.BatchCmdEncoder;
import com.fsindustry.cime.redis.protocal.handler.CmdEncoder;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

/**
 * 初始化普通协议channel处理职责链
 *
 * @author fuzhengxin
 */
public class PlainChannelInitializer extends ChannelInitializer<Channel> {

    @Override
    protected void initChannel(Channel channel) throws Exception {

        // 初始化channel职责链
        channel.pipeline()
                // 单条命令编码
                .addLast(CmdEncoder.instance())
                // 多条命令编码
                .addLast(BatchCmdEncoder.instance());
    }
}
