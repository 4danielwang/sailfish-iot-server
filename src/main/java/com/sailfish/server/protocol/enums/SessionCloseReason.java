package com.sailfish.server.protocol.enums;

/**
 * Session关闭原因枚举
 *
 * @author wangpeixin
 * @since 2025/6/26 10:22
 */
public enum SessionCloseReason {
    /**
     * 自然销毁
     */
    DESTRUCTION,

    /**
     * 失活（超时）
     */
    INACTIVE,

    /**
     * 手动销毁
     */
    MANUALLY

}
