package com.sailfish.server.protocol.provider;

import com.sailfish.server.protocol.TcpSession;

import java.util.UUID;

/**
 * session注册中心
 *
 * @author wangpeixin
 * @since 2025/6/26 16:52
 */
public interface SessionProvider {
    // 注册会话
    void register(TcpSession session);

    // 注销会话
    void unregister(UUID sessionId);

    // 获取会话
    TcpSession get(UUID sessionId);

    // 更新会话
    void refresh(TcpSession session);
}
