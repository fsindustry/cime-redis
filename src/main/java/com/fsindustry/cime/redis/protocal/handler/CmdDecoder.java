/**
 * Copyright 2018 Nikita Koksharov
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fsindustry.cime.redis.protocal.handler;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import com.fsindustry.cime.redis.exception.RedisException;
import com.fsindustry.cime.redis.exception.server.RedisAskException;
import com.fsindustry.cime.redis.exception.server.RedisBusyException;
import com.fsindustry.cime.redis.exception.server.RedisClusterDownException;
import com.fsindustry.cime.redis.exception.server.RedisMovedException;
import com.fsindustry.cime.redis.exception.server.RedisNoscriptException;
import com.fsindustry.cime.redis.protocal.cmd.Cmd;
import com.fsindustry.cime.redis.protocal.constant.ArrayDecodeStack;
import com.fsindustry.cime.redis.protocal.constant.ErrorFlag;
import com.fsindustry.cime.redis.protocal.constant.MsgType;
import com.fsindustry.cime.redis.protocal.parser.Parser;
import com.fsindustry.cime.redis.protocal.vo.BatchCmdReq;
import com.fsindustry.cime.redis.protocal.vo.BufferedReq;
import com.fsindustry.cime.redis.protocal.vo.CmdReq;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * Redis协议解析
 *
 * @author fuzhengxin
 */
@Slf4j
public class CmdDecoder extends ReplayingDecoder<ArrayDecodeStack> {

    private static final byte CR = '\r';

    private static final byte LF = '\n';

    private static final byte ZERO = '0';

    private static final byte NEGATIVE_FLAG = '-';

    public CmdDecoder() {
        // 初始化状态为就绪状态
        super();
    }

    private void sendNext(ChannelHandlerContext ctx) {
        ctx.pipeline().get(CmdBuffer.class).sendNext(ctx.channel());
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        if (log.isTraceEnabled()) {
            log.trace("channel: {} message: {}", ctx.channel(), in.toString(0, in.writerIndex(), CharsetUtil.UTF_8));
        }

        // 获取待发送请求
        BufferedReq req = ctx.channel().attr(CmdBuffer.CURRENT_COMMAND).get();
        // 如果没有待执行命令，则直接返回
        if (null == req) {
            // TODO 是否需要考虑跳过无用的字节
            return;
        }

        // 如果是单条命令，则解析单条命令
        if (req instanceof CmdReq) {
            try {

                // 初始化状态
                state(new ArrayDecodeStack());

                // 解析命令
                Object tmp = handleCmd(in);

                // 重置状态
                state(null);

                // 调用转换器对结果进行转换
                Cmd cmd = ((CmdReq) req).getCmd();
                Parser parser = cmd.getRespParser();
                Object result = parser.parse(tmp, cmd.getRespType(), ((CmdReq) req).getCodec());

                // 通知结果
                req.trySuccess(result);

            } catch (Exception e) {

                log.error("Unable to decode data. channel: {} message: {}", ctx.channel(),
                        in.toString(0, in.writerIndex(), CharsetUtil.UTF_8), e);

                // 如果命令执行异常，则执行下一条命令
                req.tryFailure(e);
                sendNext(ctx);
                throw e;
            }
        }
        // 如果是多条命令，则逐条解析
        else if (req instanceof BatchCmdReq) {
            try {

                // 初始化状态
                state(new ArrayDecodeStack());

                List<Object> results = handleBatchCmd((BatchCmdReq) req, in);
                // 重置状态
                state(null);

                // 逐条解析结果
                Throwable cause = null;
                if (null != results) {
                    List<CmdReq> cmdReqs = ((BatchCmdReq) req).getCmds();
                    for (int i = 0; i < cmdReqs.size(); i++) {
                        CmdReq cmdReq = cmdReqs.get(i);
                        // 调用转换器对结果进行转换
                        Cmd cmd = cmdReq.getCmd();
                        Parser parser = cmd.getRespParser();
                        // 解析结果
                        Object result = parser.parse(results.get(i), cmd.getRespType(), cmdReq.getCodec());

                        // 通知单条命令执行结果
                        if (cmdReq.trySuccess(result)) {
                            cause = cmdReq.cause();
                        }
                    }
                }

                // 通知批量命令执行结果
                if (cause == null) {
                    req.trySuccess(null);
                } else {
                    req.tryFailure(cause);
                }

            } catch (Exception e) {
                // 如果异常，则通知失败
                req.tryFailure(e);
                throw e;
            } finally {
                // 执行下一条命令
                sendNext(ctx);
            }
        }
        // 未知命令，直接执行下一条命令
        else {
            log.error("Unknown cmd type:" + req.getClass(), new IllegalStateException());
            sendNext(ctx);
        }
    }

    private Object handleCmd(ByteBuf in)
            throws IOException {

        // 读取命令前缀
        char typePrifix = (char) in.readByte();

        // 如果返回错误，则根据错误抛出相应异常
        if (MsgType.ERROR.getPrefix() == typePrifix) {

            String error = readString(in);

            if (error.startsWith(ErrorFlag.MOVED)) {
                String[] errorParts = error.split(" ");
                int slot = Integer.valueOf(errorParts[1]);
                String addr = errorParts[2];
                throw new RedisMovedException(slot, addr);
            } else if (error.startsWith(ErrorFlag.ASK)) {
                String[] errorParts = error.split(" ");
                int slot = Integer.valueOf(errorParts[1]);
                String addr = errorParts[2];
                throw new RedisAskException(slot, addr);
            } else if (error.startsWith(ErrorFlag.CLUSTERDOWN)) {
                throw new RedisClusterDownException(error);
            } else if (error.startsWith(ErrorFlag.BUSY)) {
                throw new RedisBusyException(error);
            } else if (error.startsWith(ErrorFlag.NOSCRIPT)) {
                throw new RedisNoscriptException(error);
            } else if (error.startsWith(ErrorFlag.TRYAGAIN)) {
                throw new RedisNoscriptException(error);
            } else if (error.startsWith(ErrorFlag.LOADING)) {
                throw new RedisNoscriptException(error);
            } else if (error.startsWith(ErrorFlag.OOM)) {
                throw new RedisNoscriptException(error);
            } else if (error.startsWith(ErrorFlag._OOM)) {
                throw new RedisNoscriptException(error);
            } else {
                throw new RedisException(error);
            }
        }

        // 解析命令
        if (MsgType.STRING.getPrefix() == typePrifix) {

            return readString(in);

        } else if (MsgType.INTEGER.getPrefix() == typePrifix) {

            return readLong(in);

        } else if (MsgType.BULK_STRING.getPrefix() == typePrifix) {

            return readBulkString(in);

        } else if (MsgType.ARRAY.getPrefix() == typePrifix) {

            return readArray(in);

        } else {
            throw new IllegalArgumentException("Illegal msg type:" + typePrifix);
        }
    }

    private String readString(ByteBuf in) throws IOException {

        ByteBuf content = in.readBytes(in.bytesBefore(CR));
        if (in.readByte() == CR && in.readByte() != LF) {
            throw new IOException("Invalid EOF");
        }

        String result = content.toString(CharsetUtil.UTF_8);
        content.release();
        return result;
    }

    private int readInt(ByteBuf in) throws IOException {

        long value = readLong(in);
        if (value > Integer.MAX_VALUE || value < Integer.MIN_VALUE) {
            throw new IllegalArgumentException("Integer overflow");
        }

        return (int) value;
    }

    private long readLong(ByteBuf in) throws IOException {

        // 判断符号
        final boolean isNag = (in.readByte() == NEGATIVE_FLAG);

        long value = 0;
        byte current;
        while (true) {

            current = in.readByte();

            // 如果读到\r\n，则跳出循环
            if (current == CR) {
                current = in.readByte();
                if (current != LF) {
                    throw new IOException("Unexpected character!");
                }
                break;
            }
            value = value * 10 + current - ZERO;
        }

        return (isNag ? -value : value);
    }

    private ByteBuf readBulkString(ByteBuf in) throws IOException {

        int size = readInt(in);
        if (size == -1) {
            return null;
        }
        ByteBuf buffer = in.readSlice(size);
        int cr = in.readByte();
        int lf = in.readByte();
        if (cr != CR || lf != LF) {
            throw new IOException("Improper line ending: " + cr + ", " + lf);
        }
        return buffer;
    }

    private List<Object> readArray(ByteBuf in) throws IOException {

        int size = readInt(in);

        // 如果数组为null，返回null
        if (size == -1) {
            return null;
        }

        // 如果数组没有元素，则返回空数组
        if (size == 0) {
            return Collections.emptyList();
        }

        // 解析前状态入栈
        ArrayDecodeStack.State currentState = new ArrayDecodeStack.State(size);
        state().push(currentState);
        checkpoint();

        for (int i = 0; i < size; i++) {
            // 解析单条数据后的数据入栈，记录保存点
            currentState.add(handleCmd(in));
            checkpoint();
        }

        // 解析完成后，状态出栈，返回结果
        return state().pop().getDatas();
    }

    private List<Object> handleBatchCmd(BatchCmdReq batchCmdReq, ByteBuf in) throws IOException {

        List<CmdReq> reqs = batchCmdReq.getCmds();
        // 如果没有待执行命令，则返回null
        if (null == reqs || reqs.isEmpty()) {
            return null;
        }

        // 解析前状态入栈
        ArrayDecodeStack.State currentState = new ArrayDecodeStack.State(reqs.size());
        state().push(currentState);
        checkpoint();

        // 逐条解析结果，直到达到预期数量
        while (currentState.idx() < currentState.getSize()) {
            try {
                // 解析单条数据后的数据入栈，记录保存点
                currentState.add(handleCmd(in));
                checkpoint();
            } catch (IOException e) {
                CmdReq req = reqs.get(currentState.idx());
                req.tryFailure(e);
                throw e;
            }
        }

        // 解析完成后，状态出栈，返回结果
        return state().pop().getDatas();
    }
}
