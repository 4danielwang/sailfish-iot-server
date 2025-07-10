package com.sailfish.server.core.enums;

/**
 * 关闭会话原因
 *
 * @author wangpeixin
 * @since 2025/7/8 14:18
 */
public enum SessionCloseReason {
    /**
     * 进程关闭 自动触发
     */
    DESTRUCTION,

    /**
     * 会话失活 会话过期
     */
    INACTIVE,

    /**
     * 手动触发
     */
    MANUALLY
}
