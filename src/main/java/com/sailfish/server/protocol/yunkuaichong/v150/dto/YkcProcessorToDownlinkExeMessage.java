package com.sailfish.server.protocol.yunkuaichong.v150.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 下行消息
 *
 * @author wangpeixin
 * @since 2025/7/9 15:47
 */
@Data
@Accessors(chain = true)
public class YkcProcessorToDownlinkExeMessage {

    // 下行消息id
    private final String id;

    // 如果是响应下行 这里可能是上行消息的id
    private String requestId;

    // 指令编码
    private int cmd;

}
