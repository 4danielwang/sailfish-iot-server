package com.sailfish.server.common.util;

import java.util.UUID;

/**
 * ID生成器
 *
 * @author wangpeixin
 * @since 2025/7/8 13:55
 */
public class IDUtil {

    // 生成随机ID
    public static String generateID() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
