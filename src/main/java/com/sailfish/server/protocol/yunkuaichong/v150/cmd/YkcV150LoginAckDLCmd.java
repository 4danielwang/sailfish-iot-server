package com.sailfish.server.protocol.yunkuaichong.v150.cmd;

import com.sailfish.server.common.annotations.CommandCode;
import com.sailfish.server.core.session.ProtocolSession;
import com.sailfish.server.protocol.yunkuaichong.v150.executor.YkcCmdDownExecutor;
import com.sailfish.server.protocol.yunkuaichong.v150.dto.YkcProcessorToDownlinkExeMessage;

/**
 * 登录请求应答
 *
 * @author wangpeixin
 * @since 2025/7/9 16:33
 */
@CommandCode(value = 0x02, desc = "登录请求应答")
public class YkcV150LoginAckDLCmd extends YkcCmdDownExecutor {
    @Override
    public void execute(YkcProcessorToDownlinkExeMessage message, ProtocolSession session) {

    }
}
