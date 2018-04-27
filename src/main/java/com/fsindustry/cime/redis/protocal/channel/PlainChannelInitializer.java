package com.fsindustry.cime.redis.protocal.channel;

import com.fsindustry.cime.redis.protocal.handler.BatchCmdEncoder;
import com.fsindustry.cime.redis.protocal.handler.CmdBuffer;
import com.fsindustry.cime.redis.protocal.handler.CmdDecoder;
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
                // 输出：单条命令编码
                .addLast(CmdEncoder.instance())
                // 输出：多条命令编码
                .addLast(BatchCmdEncoder.instance())
                // 输出：缓存请求，匹配响应结果
                .addLast(new CmdBuffer())
                // 输入：命令解码器
                .addLast(new CmdDecoder());

    }
}
