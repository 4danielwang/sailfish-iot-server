package com.sailfish.server.protocol.yunkuaichong.v150.enums;

import com.sailfish.server.core.command.Command;
import lombok.AllArgsConstructor;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 云快充协议下行指令枚举
 *
 * @author wangpeixin
 * @since 2025/7/9 08:58
 */
@AllArgsConstructor
public enum YkcDownCmdEnum implements Command {
    LOGIN_ACK("0x02", "充电桩登入应答"),
    HEARTBEAT_ACK("0x04", "心跳应答"),
    CHARGING_EVENT_ACK("0x06", "充电事件应答"),
    STATUS_REPORT_ACK("0x08", "状态信息应答"),
    REMOTE_START_CHARGE("0x0A", "远程启动充电"),
    REMOTE_STOP_CHARGE("0x0C", "远程停止充电");
    ;

    private final String code;

    private final String desc;

    private static final Map<String, YkcDownCmdEnum> VALUE_MAP = Arrays.stream(values())
            .collect(Collectors.toMap(YkcDownCmdEnum::getCode, Function.identity()));


    // 根据命令字符串获取对应的枚举
    public static YkcDownCmdEnum of(String cmd) {
        return Optional.ofNullable(VALUE_MAP.get(cmd))
                .orElseThrow(() -> new IllegalArgumentException("Unknown YkcDownCmdEnum: " + cmd));
    }

    public Short getShortCode() {
        return Short.decode(getCode());
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDescription() {
        return desc;
    }
}
