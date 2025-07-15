package com.sailfish.server.core.boostrap;

import com.sailfish.server.core.broker.Forwarder;
import com.sailfish.server.core.listener.ServerListener;
import com.sailfish.server.core.protocol.MessageProcessor;
import com.sailfish.server.core.protocol.ProtocolContext;
import lombok.extern.slf4j.Slf4j;

/**
 * 抽象启动器类
 *
 * @author wangpeixin
 * @since 2025/7/9 14:47
 */
@Slf4j
public abstract class AbstractBootstrap{

    // 服务器全局上下文
    protected ProtocolContext context;

    // 服务器监听器
    protected ServerListener listener;

    // 消息转发器
    protected Forwarder forwarder;

    /**
     * 启动服务器
     *
     * @author wangpeixin
     * @since 2025/7/9 15:14
     */
    public void init() throws InterruptedException {
        log.info("[{}] Server Initializing...");

        listener = new ServerListener(getProtocolName(), messageProcessor());
        listener.startListener();
        _init();
    }

    /**
     * 销毁服务器
     *
     * @author wangpeixin
     * @since 2025/7/9 15:15
     */
    public void destroy() throws InterruptedException {
        log.info("[{}] Server Destroying...");

        if(listener != null) {
            listener.destroy();
        }
        // TODO：销毁其他资源
        _destroy();
    }

    // 获取协议名称
    public abstract String getProtocolName();

    // 获取消息处理器
    protected abstract MessageProcessor messageProcessor();

    protected void _init(){

    }

    protected void _destroy(){

    }

}
