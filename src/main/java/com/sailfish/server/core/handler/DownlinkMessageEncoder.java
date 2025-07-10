package com.sailfish.server.core.handler;

import com.sailfish.server.core.protocol.MessageProcessor;
import com.sailfish.server.core.dto.req.DownlinkMessageReq;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 下行消息（业务消息对象）编码器
 *
 * @author peixin wang
 */
@Slf4j
@RequiredArgsConstructor
public class DownlinkMessageEncoder extends MessageToByteEncoder<DownlinkMessageReq> {

    private final String protocolName;

    private final MessageProcessor processor;

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, DownlinkMessageReq req, ByteBuf byteBuf) throws Exception {

    }
}
