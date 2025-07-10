package com.sailfish.server.core.protocol;

import com.sailfish.server.core.session.SessionManager;
import io.netty.util.ResourceLeakDetector;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;

/**
 * 服务器上下文
 *
 * @author wangpeixin
 * @since 2025/7/8 15:17
 */
@Slf4j
@RequiredArgsConstructor
@Getter
public class ProtocolContext {

    // 会话注册中心
    private final SessionManager sessionManager;

    @PostConstruct
    public void init() {
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.SIMPLE);
        log.info("ProtocolContext initialized successfully.");
    }
}
