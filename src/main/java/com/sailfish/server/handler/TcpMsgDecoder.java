package com.sailfish.server.handler;

import com.sailfish.server.net.TcpUplinkMsg;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


/**
 * TCP报文解码器
 * Bytebuf-> TcpUplinkMsg
 * @author wangpeixin
 * @since 2025/6/23 10:51
 */
@Slf4j
public class TcpMsgDecoder extends MessageToMessageDecoder<ByteBuf> {

    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) {
        try {
            byte[] in = toByteArray(msg);

            if(log.isDebugEnabled()){
                log.debug("起始标志: {}, {}", in[0],  String.format("[%02x]", in[0]));
                log.debug("数据长度: {}, {}", in[1], String.format("[%02x]", in[1]));
                log.debug("序列号: {}, {}", Arrays.copyOfRange(in, 2,4), String.format("[%02x %02x]", in[2], in[3]));
                log.debug("flag: {}, {}", in[4], String.format("[%02x]", in[4]));
                log.debug("帧类型: {}, {}", in[5], String.format("[%02x]", in[5]));
                byte[] body = Arrays.copyOfRange(in, 6, in.length - 2);
                log.debug("消息体: {}, {}", body, new String(body, StandardCharsets.UTF_8));

                log.debug("crc: {}, {}",Arrays.copyOfRange(in, in.length-2, in.length),
                        String.format("[%02x %02x]", in[in.length-2], in[in.length-1]));
            }

            String res = toString(msg, StandardCharsets.UTF_8.name());
            log.info("[{}]{} decode message: {},", ctx.channel(), ctx.channel().remoteAddress(), res);
            // 包装消息 向下游传递
            out.add(new TcpUplinkMsg(UUID.randomUUID(), in));
        } catch (Exception e) {
            log.error("[{}] Exception during of decoding message",ctx.channel(), e);
            throw new RuntimeException(e);
        }
    }

    // Bytebuf转字节数组
    public static byte[] toByteArray(ByteBuf buffer) {
        byte[] bytes = new byte[buffer.readableBytes()];
        buffer.readBytes(bytes);
        return bytes;
    }

    // 打印出ByteBuf内容
    public static String toString(ByteBuf buffer, String charsetName) {
        return buffer.toString(Charset.forName(charsetName));
    }

}
