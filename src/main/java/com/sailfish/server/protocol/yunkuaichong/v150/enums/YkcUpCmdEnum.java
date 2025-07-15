package com.sailfish.server.protocol.yunkuaichong.v150.enums;

import com.sailfish.server.core.command.Command;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 云快充协议上行指令枚举
 *
 * @author wangpeixin
 * @since 2025/7/9 08:59
 */
@AllArgsConstructor
@Getter
public enum YkcUpCmdEnum implements Command {
    LOGIN("0x01", "充电桩登入"),
    HEARTBEAT("0x03", "心跳"),
    MODEL_VALIDATE("0x05", "计费模型验证请求"),
    PRICE_MODEL("0x09", "计费模型请求"),
    MONITOR_DATA("0x13", "实时监测数据上传"),
    ;

    private final String code;

    private final String desc;

    private static final Map<String, YkcUpCmdEnum> VALUE_MAP = Arrays.stream(values())
            .collect(Collectors.toMap(YkcUpCmdEnum::getCode, Function.identity()));

    // 根据命令字符串获取对应的枚举
    public static YkcUpCmdEnum of(String cmd) {
        return Optional.ofNullable(VALUE_MAP.get(cmd))
                .orElseThrow(() -> new IllegalArgumentException("Unknown YkcUpCmdEnum: " + cmd));
    }

    public short getShortCode() {
        return Short.decode(code);
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
