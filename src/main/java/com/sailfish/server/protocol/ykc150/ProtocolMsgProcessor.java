package com.sailfish.server.protocol.ykc150;

import com.sailfish.server.ProtocolContext;
import com.sailfish.server.forwarder.AbstractMsgForwarder;
import com.sailfish.server.net.TcpDownlinkMsg;
import com.sailfish.server.net.TcpUplinkMsg;
import com.sailfish.server.protocol.TcpSession;
import lombok.extern.slf4j.Slf4j;

/**
 * 协议处理器（核心类）
 * 处理上行消息
 * 处理下行消息
 *
 * @author wangpeixin
 * @since 2025/6/24 13:40
 */
@Slf4j
public abstract class ProtocolMsgProcessor {

    // 消息转发器
    protected final AbstractMsgForwarder forwarder;

    // 全局上下文
    protected final ProtocolContext ctx;

    protected ProtocolMsgProcessor(AbstractMsgForwarder forwarder, ProtocolContext ctx) {
        this.forwarder = forwarder;
        this.ctx = ctx;
    }

    // 上行处理核心逻辑 子类实现
    // 帧格式校验 crc校验
    protected abstract void handleUplink0(TcpUplinkMsg msg, TcpSession session);

    // 下行处理核心逻辑
    // 帧格式校验 crc
    protected abstract void handleDownlink0(TcpDownlinkMsg msg, TcpSession session);

    // 处理上行消息
    public void handleUplink(TcpUplinkMsg msg, TcpSession session) {
        try {
            handleUplink0(msg, session);
        } catch (Exception e) {
            log.error("消息处理器处理报文异常: {}", msg, e);
        }

    }

    // 处理下行消息
    public void handleDownlink(TcpDownlinkMsg msg) {
        try {
            TcpSession session = ctx.getSessionProvider().get(msg.id());
            handleDownlink0(msg, session);
        } catch (Exception e) {
            log.error("消息处理器处理报文异常: {}", msg, e);
        }
    }

}
