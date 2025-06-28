package com.sailfish.server.command.executor;

import com.sailfish.server.ProtocolContext;
import com.sailfish.server.command.domain.YkcUplinkMsg;
import com.sailfish.server.protocol.TcpSession;

/**
 * 命令执行器接口
 *
 * @param <T> 命令类型
 * @author wangpeixin
 * @since 2025/6/27 16:34
 */
public interface ICmdExecutor<T>{
    /**
     * 执行命令
     */
    void execute(TcpSession session, T msg, ProtocolContext ctx);

}
