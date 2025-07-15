package com.sailfish.server.protocol.yunkuaichong.v150;

import com.sailfish.server.core.boostrap.AbstractBootstrap;
import com.sailfish.server.core.protocol.MessageProcessor;
import com.sailfish.server.core.protocol.ProtocolContext;
import com.sailfish.server.protocol.yunkuaichong.v150.executor.YkcMessageProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

/**
 * 云快充1.5协议启动器
 *
 * @author wangpeixin
 * @since 2025/7/9 14:49
 */
@RequiredArgsConstructor
@Component
public class YkcV150Bootstrap extends AbstractBootstrap implements CommandLineRunner{

    private final String protocolName = "YKC150";

    private final ProtocolContext context;

    @Override
    public String getProtocolName() {
        return protocolName;
    }

    @Override
    protected MessageProcessor messageProcessor() {
        return new YkcMessageProcessor(forwarder, context);
    }

    @Async
    @Override
    public void run(String... args) throws Exception {
        init();
    }
}
