package com.sailfish.server.core.protocol;

import com.sailfish.server.core.broker.Forwarder;
import com.sailfish.server.core.dto.DecoderToProcessorMessage;
import com.sailfish.server.core.dto.EncoderToProcessorMessage;
import lombok.extern.slf4j.Slf4j;

/**
 * 消息处理器
 *
 * @author wangpeixin
 * @since 2025/7/8 15:13
 * @description 用于处理上行和下行消息的抽象类 处理模版
 */
@Slf4j
public abstract class MessageProcessor {

    protected final Forwarder forwarder;

    protected final ProtocolContext protocolContext;

    protected MessageProcessor(Forwarder forwarder, ProtocolContext protocolContext) {
        this.forwarder = forwarder;
        this.protocolContext = protocolContext;
    }

    // 上行消息模版
    public void uplink(DecoderToProcessorMessage msg) {
        // TODO：异步处理
        try {
            uplink0(msg);
        } catch (Exception e) {
            log.error("{} 消息处理器处理报文异常", msg.session(), e);
        }
    }

    // 下行消息模版
    public void downlink(EncoderToProcessorMessage msg) {
        try {
            downlink0(msg);
        } catch (Exception e) {
            log.error("{} 消息处理器处理报文异常", msg.session(), e);
        }

    }

    // 子类实现 上行处理方法
    protected abstract void uplink0(DecoderToProcessorMessage msg);

    // 子类实现 下行处理方法
    protected abstract void downlink0(EncoderToProcessorMessage msg);

}
