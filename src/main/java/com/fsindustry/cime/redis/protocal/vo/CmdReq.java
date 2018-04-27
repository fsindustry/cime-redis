package com.fsindustry.cime.redis.protocal.vo;

import com.fsindustry.cime.redis.protocal.cmd.Cmd;
import com.fsindustry.cime.redis.protocal.codec.Codec;

import io.netty.util.concurrent.Promise;
import lombok.Getter;
import lombok.Setter;

/**
 * 用于传递命令参数
 *
 * @param <In>  输入类型
 * @param <Out> 返回类型
 *
 * @author fuzhengxin
 */
@Getter
@Setter
public class CmdReq<In, Out> implements BufferedReq {

    /**
     * 异步接收命令结果的promise对象
     */
    private Promise<Out> resultPromise;

    /**
     * 待执行redis命令
     */
    private Cmd<In> cmd;

    /**
     * 命令参数
     * 可以是一个ByteBuf
     * 可以是一个byte[]
     * 可以是一个包含toString方法的对象
     */
    private Object[] params;

    /**
     * 数据序列化编解码器
     */
    private Codec codec;

    public CmdReq(Promise<Out> resultPromise, Cmd<In> cmd, Object[] params,
                  Codec codec) {
        this.resultPromise = resultPromise;
        this.cmd = cmd;
        this.params = params;
        this.codec = codec;
    }

    @Override
    public boolean tryFailure(Throwable cause) {
        return resultPromise.tryFailure(cause);
    }

    @Override
    public boolean trySuccess(Object result) {
        return resultPromise.trySuccess((Out) result);
    }

    @Override
    public Throwable cause() {
        return resultPromise.cause();
    }

    @Override
    public boolean isSuccess() {
        return resultPromise.isSuccess();
    }
}
