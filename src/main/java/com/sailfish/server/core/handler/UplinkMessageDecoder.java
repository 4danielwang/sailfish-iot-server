package com.sailfish.server.core.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.sailfish.server.core.protocol.MessageProcessor;
import com.sailfish.server.core.dto.DecoderToProcessorMessage;
import com.sailfish.server.core.dto.ProtocolUplinkMessage;
import com.sailfish.server.core.session.ProtocolSession;
import com.sailfish.server.common.util.json.JacksonUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * 上行消息解码器
 *
 * @param <T> 解码后的消息类型
 * @author peixin wang
 * @description 把ProtocolMessage解码为UplinkMessage
 */
@Slf4j
@RequiredArgsConstructor
public class UplinkMessageDecoder<T> extends SimpleChannelInboundHandler<ProtocolUplinkMessage<T>> {

    private final String protocolName;

    private final MessageProcessor handler;

    private final ProtocolSession session;

    // TODO: 指标记录
    public UplinkMessageDecoder(String protocolName, MessageProcessor handler) {
        this.protocolName = protocolName;
        this.handler = handler;
        this.session = new ProtocolSession(protocolName);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ProtocolUplinkMessage<T> msg) throws Exception {
        if (log.isDebugEnabled()) {

            log.debug("[{}]{}{} Netty拆出上行报文:{}", protocolName, ctx.channel(), session, msg);
        }

        session.refreshLastActiveTime();

        if (session.getIp() == null) {

            session.setIp(msg.ip());
        }

        if (session.getCtx() == null) {

            session.setCtx(ctx);
        }

        T data = msg.content();

        if (Objects.isNull(data)) {

            log.debug("[{}]{}{} 上行报文为空被过滤 [{}]", protocolName, ctx.channel(), session, msg);

            return;
        }

        try {

            process(msg, ctx);

        } catch (Exception e) {

            log.error("[{}]{}{} TCP管道处理报文异常", protocolName, ctx.channel(), session, e);
        }
    }

    /**
     * ProtocolUplinkMessage -> DecoderToProcessorMessage
     * 支持 JSON、String、byte[] 等类型的上行报文
     *
     * @author wangpeixin
     * @since 2025/7/8 14:50
     */
    private void process(ProtocolUplinkMessage<T> msg, ChannelHandlerContext ctx) {
        switch (msg.content()) {
            case byte[] bytes ->
                    handler.uplink(new DecoderToProcessorMessage(msg.id(), bytes, session));
            case JsonNode json ->
                    handler.uplink(new DecoderToProcessorMessage(msg.id(), JacksonUtil.writeValueAsBytes(json), session));
            case String text ->
                    handler.uplink(new DecoderToProcessorMessage(msg.id(), JacksonUtil.writeValueAsBytes(text.getBytes()), session));
            case null, default -> {
                assert msg.content() != null;
                log.warn("[{}]{}{} 不支持的TCP上行报文类型:{}", protocolName, ctx.channel(), session, msg.content().getClass());
            }
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {

        ctx.flush();

        if (log.isTraceEnabled()) {
            log.trace("[{}]{}{} Channel Read Complete [{}]", protocolName, ctx.channel(), session, ctx.name());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("[{}]{}{} Invalid message received, Exception caught", protocolName, ctx.channel(), session, cause);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {

        super.channelRegistered(ctx);

        log.info("[{}]{} 打开通道", protocolName, ctx.channel());
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {

        super.channelUnregistered(ctx);

        log.info("[{}]{}{} 关闭通道", protocolName, ctx.channel(), session);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        super.channelActive(ctx);

        log.info("[{}]{} 通道活跃", protocolName, ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

        super.channelInactive(ctx);

        log.info("[{}]{}{} 通道不活跃", protocolName, ctx.channel(), session);
    }

}
