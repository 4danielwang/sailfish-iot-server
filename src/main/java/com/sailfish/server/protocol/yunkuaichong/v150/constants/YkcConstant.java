package com.sailfish.server.protocol.yunkuaichong.v150.constants;

/**
 * 云快充协议常量
 *
 * @author wangpeixin
 * @since 2025/7/9 16:17
 */
public class YkcConstant {
    // 起始标志
    public static final byte YUNKUAICHONG_HEAD = 0x68;

    // 加密标志
    public static final int YUNKUAICHONG_NORMAL_ENCRYPTION_FLAG = 0;

    // 加密标志
    public static final int YUNKUAICHONG_NORMAL_ENCRYPTION_FLAG_TRUE = 1;

    // ack响应成功
    public static final int YUNKUAICHONG_ACK_SUCCESS = 0;

    // ack响应失败
    public static final int YUNKUAICHONG_ACK_FAILURE = 1;

    // 计费模型验证成功
    public static final int YUNKUAICHONG_PRICE_VALIDATE_SUCCESS = 0;

    // 计费模型验证失败
    public static final int YUNKUAICHONG_PRICE_VALIDATE_FAILURE = 1;

    public static final byte TOP_BYTE = 0x00;
    public static final byte PEAK_BYTE = 0x01;
    public static final byte FLAT_BYTE = 0x02;
    public static final byte VALLEY_BYTE = 0x03;
}
