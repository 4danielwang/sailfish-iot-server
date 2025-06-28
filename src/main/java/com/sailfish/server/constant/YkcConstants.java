package com.sailfish.server.constant;

/**
 * 云快充协议常量
 *
 * @author wangpeixin
 * @since 2025/6/26 12:49
 */
public final class YkcConstants {

    // 起始标志
    public static final byte YUNKUAICHONG_HEAD = 0x68;

    // 加密标志
    public static final int YUNKUAICHONG_ENCRYPT_NO = 0x00;

    // 登录成功响应
    public static final byte YUNKUAICHONG_LOGIN_ACK_SUCCESS = 0x00;

    // 登录成功响应
    public static final byte YUNKUAICHONG_LOGIN_ACK_FAIL = 0x01;
}
