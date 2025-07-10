package com.sailfish.server.protocol.yunkuaichong.v150.executor;

import com.sailfish.server.core.protocol.ProtocolContext;
import com.sailfish.server.core.session.ProtocolSession;
import com.sailfish.server.protocol.yunkuaichong.v150.dto.YkcProcessorToUplinkExeMessage;

/**
 * 云快充上行执行器
 *
 * @author wangpeixin
 * @since 2025/7/9 16:34
 */
public abstract class YkcCmdUpExecutor extends YkcCmdExecutor{

    /**
     * 执行指令
     *
     * @author wangpeixin
     */
    public abstract void execute(YkcProcessorToUplinkExeMessage message, ProtocolSession session, ProtocolContext ctx);

}
