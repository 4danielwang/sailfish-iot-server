package com.sailfish.server.core.session;


/**
 * 会话管理器接口
 */
public interface SessionManager {

    /**
     * 注册一个新的会话
     *
     * @param protocolSession 新的会话
     */
    void register(ProtocolSession protocolSession);

    /**
     * 根据会话ID移除会话
     *
     * @param sessionId 会话ID
     * @return 被移除的会话，如果不存在则返回null
     */
    void unregister(String sessionId);

    /**
     * 根据会话ID获取会话
     *
     * @param sessionId 会话ID
     * @return 对应的会话，如果不存在则返回null
     */
    ProtocolSession get(String sessionId);

    /**
     * 刷新会话状态
     *
     * @author wangpeixin
     * @since 2025/7/9 11:39
     */
    void refreshSession(String sessionId);

    // TODO：支持一个边缘网关 多个设备

}
