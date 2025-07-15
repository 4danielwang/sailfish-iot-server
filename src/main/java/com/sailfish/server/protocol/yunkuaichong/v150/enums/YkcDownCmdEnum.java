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
    MODEL_VALIDATE_ACK("0x06", "计费模型验证请求应答"),
    PRICE_MODEL_ACK("0x0A", "计费模型请求应答"),
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
