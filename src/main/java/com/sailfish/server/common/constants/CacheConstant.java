package com.sailfish.server.common.constants;

/**
 * 缓存常量
 *
 * @author wangpeixin
 * @since 2025/7/15 09:22
 */
public class CacheConstant {
    // 云快充上行ack缓存前缀
    public static final String CACHE_UPLINK_ACK = "yunkuaichong:uplink_ack:%04X:%02X";

    public static void main(String[] args) {
        // 测试输出缓存前缀
        System.out.println(String.format(CACHE_UPLINK_ACK, 1024, 0x01));
    }
}
