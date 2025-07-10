package com.sailfish.server.protocol.yunkuaichong.v150;

import com.sailfish.server.core.boostrap.AbstractBootstrap;
import com.sailfish.server.core.protocol.MessageProcessor;
import com.sailfish.server.core.protocol.ProtocolContext;
import com.sailfish.server.core.session.DefaultSessionManager;
import com.sailfish.server.protocol.yunkuaichong.v150.executor.YkcMessageProcessor;

/**
 * 云快充1.5协议启动器
 *
 * @author wangpeixin
 * @since 2025/7/9 14:49
 */
public class YkcV150Bootstrap extends AbstractBootstrap {

    private final String protocolName = "YKC150";

    public YkcV150Bootstrap() {
        this.context = new ProtocolContext(DefaultSessionManager.getInstance());
    }

    @Override
    public String getProtocolName() {
        return protocolName;
    }

    @Override
    protected MessageProcessor messageProcessor() {
        return new YkcMessageProcessor(forwarder, context);
    }

    // 测试启动云快充服务器
    public static void main(String[] args) throws InterruptedException {
        new YkcV150Bootstrap().init();
    }

}
