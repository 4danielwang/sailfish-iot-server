package com.sailfish.server.protocol.provider;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.sailfish.server.async.ExecutorsFactory;
import com.sailfish.server.protocol.TcpSession;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 默认session注册中心实现
 *
 * @author wangpeixin
 * @since 2025/6/26 16:55
 */
@Slf4j
public class DefaultSessionProvider implements SessionProvider{

    private static final int INIT_CACHE_LIMIT = 100_000;
    private static final int MAXIMUM_SIZE = 1_000_000;

    private final Cache<UUID, TcpSession> sessionMap = buildMap();


    // TODO: 定时任务 关闭超时session并注销

    private Cache<UUID, TcpSession> buildMap() {
        return Caffeine.newBuilder()
                .initialCapacity(INIT_CACHE_LIMIT)
                .maximumSize(MAXIMUM_SIZE)
                .executor(ExecutorsFactory.newVirtualThreadPool("common-virtual")) // 使用虚拟线程
                .build();
    }

    @Override
    public void register(TcpSession session) {
        if (log.isDebugEnabled()) {
            log.debug("注册 session {}", session);
        }

        sessionMap.put(session.getId(), session);
    }

    @Override
    public void unregister(UUID sessionId) {
        log.info("注销 session {}", sessionId);

        sessionMap.invalidate(sessionId);
    }

    @Override
    public TcpSession get(UUID sessionId) {
        log.debug("访问 session {}", sessionId);

        return sessionMap.get(sessionId, uuid -> null);
    }

    @Override
    public void refresh(TcpSession session) {
        if (log.isDebugEnabled()) {
            log.debug("刷新 session {}", session);
        }

        session.setLastActivityTime(LocalDateTime.now());

        sessionMap.put(session.getId(), session);
    }
}
