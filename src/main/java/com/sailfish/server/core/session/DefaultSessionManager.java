package com.sailfish.server.core.session;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.sailfish.server.autoconfigure.config.ThreadPoolConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认的会话管理器实现
 */
@Slf4j
@Component
public class DefaultSessionManager implements SessionManager {
    private static final int INIT_SIZE = 100_000;
    private static final int MAXIMUM_SIZE = 1_000_000;

    private static final DefaultSessionManager INSTANCE = new DefaultSessionManager();

    private DefaultSessionManager() {}

    public static DefaultSessionManager getInstance() {
        return INSTANCE;
    }

    //  充电桩编号 -> 会话ID
    private final Map<String, String> pileNoToSessionIdMap = new ConcurrentHashMap<>();

    // 全局会话表 会话ID -> 会话对象
    private final Cache<String, ProtocolSession> sessionCache = Caffeine.newBuilder()
            .initialCapacity(INIT_SIZE)
            .maximumSize(MAXIMUM_SIZE)
            .executor(ThreadPoolConfiguration.COMMON_IO_THREAD_POOL)
            .build();

    @PostConstruct
    public void init() {
        log.info("DefaultSessionManager initialized successfully.");
        // TODO: 开始定时任务，检查会话状态，关闭超时的会话
    }

    @PreDestroy
    public void destroy() {
        // TODO：关闭资源
        log.info("DefaultSessionManager destroyed successfully.");
    }


    @Override
    public void register(ProtocolSession protocolSession) {
        log.info("Registering session {}", protocolSession);

        sessionCache.put(protocolSession.getId(), protocolSession);
    }

    @Override
    public void unregister(String sessionId) {
        log.info("Unregistering session {}", sessionId);

        sessionCache.invalidate(sessionId);
    }

    @Override
    public ProtocolSession get(String sessionId) {
        log.info("Get session {}", sessionId);

        return sessionCache.get(sessionId, uuid -> null);
    }

    @Override
    public void refreshSession(String sessionId) {
        log.info("Refreshing session {}", sessionId);

        ProtocolSession session = get(sessionId);
        if (session != null) {
          session.refreshLastActiveTime();
        } else {
            log.warn("Session {} not found for refresh", sessionId);
        }
    }

}
