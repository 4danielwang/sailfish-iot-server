package com.sailfish.server;

import com.sailfish.server.protocol.provider.SessionProvider;
import io.netty.util.ResourceLeakDetector;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


/**
 * 服务器全局上下文
 *
 * @author wangpeixin
 * @since 2025/6/26 13:04
 */
@Data
@Slf4j
@AllArgsConstructor
public class ProtocolContext {

    // session注册中心
    private final SessionProvider sessionProvider;

    @PostConstruct
    public void init(){
        // 关闭netty资源泄漏检测
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.DISABLED);
        log.info("Setting resource leak detector level to {}", ResourceLeakDetector.Level.DISABLED);
    }
}
