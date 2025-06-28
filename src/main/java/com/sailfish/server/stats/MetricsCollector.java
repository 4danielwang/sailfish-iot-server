package com.sailfish.server.stats;

import lombok.Data;

/**
 * 指标收集器
 *
 * @author wangpeixin
 * @since 2025/6/24 13:35
 */
@Data
public class MetricsCollector {
    // 上行消息状态
    private final MessagesStats uplinkMsgStats;

    // 下行消息状态
    private final MessagesStats downlinkMsgStats;
}
