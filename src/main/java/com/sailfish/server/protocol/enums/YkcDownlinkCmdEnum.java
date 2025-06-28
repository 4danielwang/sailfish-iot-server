package com.sailfish.server.protocol.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 云快充协议下行指令枚举
 *
 * @author wangpeixin
 * @since 2025/6/25 08:44
 */
@Getter
@AllArgsConstructor
public enum YkcDownlinkCmdEnum {
    // 登录响应
    LOGIN_ACK(0x02),

    // 同步时间
    SYNC_TIME(0x56),

    // 心跳
    HEARTBEAT(0x04),

    VERIFY_PRICING_ACK(0x06),

    QUERY_PRICING_ACK(0X0A),

    SET_PRICING(0x58),

    // 启动
    REMOTE_START_CHARGING(0x34),

    // 停止
    REMOTE_STOP_CHARGING(0x36),

    TRANSACTION_RECORD(0x40),

    REMOTE_PARALLEL_START_CHARGING(0xA4);

    private final Integer cmd;
}
