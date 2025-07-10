package com.sailfish.server.protocol.yunkuaichong.v150.executor;

import com.sailfish.server.core.session.ProtocolSession;
import com.sailfish.server.protocol.yunkuaichong.v150.dto.YkcProcessorToDownlinkExeMessage;

/**
 * 云快充下行执行器
 *
 * @author wangpeixin
 * @since 2025/7/9 16:34
 */
public abstract class YkcCmdDownExecutor extends YkcCmdExecutor{


    /**
     * 执行指令
     *
     * @author wangpeixin
     */
    public abstract void execute(YkcProcessorToDownlinkExeMessage message, ProtocolSession session);

}
