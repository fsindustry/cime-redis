package com.fsindustry.cime.redis.protocal.handler;

import com.fsindustry.cime.redis.protocal.constant.MsgType;
import com.fsindustry.cime.redis.protocal.util.BytesConverter;
import com.fsindustry.cime.redis.protocal.vo.CmdReq;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 单条命令编码器
 *
 * @author fuzhengxin
 */
@Slf4j
@ChannelHandler.Sharable
public class CmdEncoder extends MessageToByteEncoder<CmdReq<?, ?>> {

    /**
     * 消息分隔符，统一使用\r\n
     */
    private static final byte[] CRLF = "\r\n".getBytes();

    /**
     * 私有化构造器
     */
    private CmdEncoder() {
    }

    /**
     * 懒汉式单实例
     */
    private static class Singleton {
        private final static CmdEncoder INSTANCE = new CmdEncoder();
    }

    /**
     * 获取单例
     */
    public static CmdEncoder instance() {
        return Singleton.INSTANCE;
    }

    /**
     * 重写write方法，捕获异常设置给promise对象
     */
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {

        if (acceptOutboundMessage(msg)) {
            if (!promise.setUncancellable()) {
                return;
            }
        }

        try {
            super.write(ctx, msg, promise);
        } catch (Exception e) {
            promise.tryFailure(e);
            throw e;
        }
    }

    /**
     * 将请求命令编码为redis请求协议格式
     * <p>
     * redis请求格式固定，都是bulk_str的数组；
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, CmdReq<?, ?> msg, ByteBuf out) throws Exception {

        try {

            // 写入数组前缀
            out.writeByte(MsgType.ARRAY.getPrefix());

            // 根据请求参数个数确定数组长度：
            // +1是因为包含命令本身
            int len = msg.getParams().length + 1;

            // 如果包含子命令，则+1
            boolean containSubCmd = !StringUtil.isNullOrEmpty(msg.getCmd().getSubName());
            if (containSubCmd) {
                len++;
            }

            // 写入数组长度
            out.writeBytes(BytesConverter.convert(len));
            out.writeBytes(CRLF);

            // 写入命令名/子命令名
            writeBulk(out, msg.getCmd().getName().getBytes(CharsetUtil.UTF_8));
            if (containSubCmd) {
                writeBulk(out, msg.getCmd().getSubName().getBytes(CharsetUtil.UTF_8));
            }

            // 写入参数
            for (Object param : msg.getParams()) {
                ByteBuf buf = encodeParam(param);
                writeBulk(out, buf);

                // 显式释放缓存；
                // 如果是ByteBuf类型由于是直接返回引用，故不清空；
                if (!(param instanceof ByteBuf)) {
                    buf.release();
                }
            }

            if (log.isTraceEnabled()) {
                log.trace("channel: {} message: {}", ctx.channel(), out.toString(CharsetUtil.UTF_8));
            }

        } catch (Exception e) {
            msg.tryFailure(e);
            throw e;
        }
    }

    /**
     * 将参数转换为ByteBuf
     */
    private ByteBuf encodeParam(Object param) {

        // 如果是byte[]
        if (param instanceof byte[]) {
            byte[] payload = (byte[]) param;
            ByteBuf out = ByteBufAllocator.DEFAULT.buffer(payload.length);
            out.writeBytes(payload);
            return out;
        }

        // 如果是ByteBuf，直接返回引用
        if (param instanceof ByteBuf) {
            return (ByteBuf) param;
        }

        // 其它参数均转化为string后放入ByteBuf
        String payload = param.toString();
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(ByteBufUtil.utf8MaxBytes(payload));
        ByteBufUtil.writeUtf8(buf, payload);
        return buf;
    }

    /**
     * 将数据编码为bulk string协议
     */
    private void writeBulk(ByteBuf out, byte[] arg) {
        out.writeByte(MsgType.BULK_STRING.getPrefix());
        out.writeBytes(BytesConverter.convert(arg.length));
        out.writeBytes(CRLF);
        out.writeBytes(arg);
        out.writeBytes(CRLF);
    }

    private void writeBulk(ByteBuf out, ByteBuf arg) {
        out.writeByte(MsgType.BULK_STRING.getPrefix());
        out.writeBytes(BytesConverter.convert(arg.readableBytes()));
        out.writeBytes(CRLF);
        out.writeBytes(arg, arg.readerIndex(), arg.readableBytes());
        out.writeBytes(CRLF);
    }

}
