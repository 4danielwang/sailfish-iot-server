package com.sailfish.server.core.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.sailfish.server.core.dto.ProtocolUplinkMessage;
import com.sailfish.server.common.util.IDUtil;
import com.sailfish.server.common.util.json.JacksonUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.util.List;
import java.util.function.Function;

/**
 * 协议解码器
 *
 * @param <T> 解码后的消息类型
 * @author peixin wang
 * @description 把bytebuf解码为T
 */
@Slf4j
@RequiredArgsConstructor
public class ProtocolDecoder<T> extends MessageToMessageDecoder<ByteBuf> {

    // 协议名称 用于日志记录当前服务器是什么协议
    private final String protocolName;

    // 消息转换函数 ByteBuf -> T
    private final Function<ByteBuf, T> transformer;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        try {
            ProtocolUplinkMessage<T> msg = new ProtocolUplinkMessage<>(ctx.channel().remoteAddress(),
                    IDUtil.generateID(),
                    this.transformer.apply(in),
                    in.readableBytes());
            out.add(msg);
        } catch (Exception e) {
            log.error("[{}][{}] Exception during of decoding message", protocolName, ctx.channel(), e);
            throw new RuntimeException(e);
        }

    }

    // transformer 转byte[]
    public static byte[] toByteArray(ByteBuf buffer) {
        byte[] bytes = new byte[buffer.readableBytes()];
        buffer.readBytes(bytes);
        return bytes;
    }

    // transformer 转string
    public static String toString(ByteBuf buffer, String charsetName) {
        return buffer.toString(Charset.forName(charsetName));
    }

    // transformer 转json
    public static JsonNode toJson(ByteBuf buffer) {
        return JacksonUtil.fromBytes(toByteArray(buffer));
    }

}
