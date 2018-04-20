package com.fsindustry.cime.redis.protocal.req;

import java.util.List;

import io.netty.util.concurrent.Promise;
import lombok.Getter;
import lombok.Setter;

/**
 * 批量命令数据
 *
 * @author fuzhengxin
 */
@Getter
@Setter
public class BatchCmdReq implements Req {

    /**
     * 异步接收命令结果的promise对象
     */
    private Promise<Void> resultPromise;

    /**
     * 待执行redis命令
     */
    private List<CmdReq> cmds;

    private final boolean skipResult;

    private final boolean atomic;

    public BatchCmdReq(Promise<Void> resultPromise, List<CmdReq> cmds) {
        this(resultPromise, cmds, false, false);
    }

    public BatchCmdReq(Promise<Void> resultPromise, List<CmdReq> cmds, boolean skipResult, boolean atomic) {
        this.resultPromise = resultPromise;
        this.cmds = cmds;
        this.skipResult = skipResult;
        this.atomic = atomic;
    }

    @Override
    public boolean tryFailure(Throwable cause) {
        return resultPromise.tryFailure(cause);
    }
}
