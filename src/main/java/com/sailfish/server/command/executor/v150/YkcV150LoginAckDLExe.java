package com.sailfish.server.command.executor.v150;

import com.sailfish.server.ProtocolContext;
import com.sailfish.server.command.domain.YkcDownlinkMsg;
import com.sailfish.server.command.executor.AbstractYkcDownlinkCmdExe;
import com.sailfish.server.protocol.TcpSession;
import lombok.extern.slf4j.Slf4j;

/**
 * 云快充1.5登录响应下行处理器
 *
 * @author wangpeixin
 * @since 2025/6/26 16:38
 */
@Slf4j
public class YkcV150LoginAckDLExe extends AbstractYkcDownlinkCmdExe {
    @Override
    public void execute(TcpSession session, YkcDownlinkMsg msg, ProtocolContext ctx) {
        log.debug("{} 云快充1.5.0登录认证应答", session);

        // 构建为LoginResp响应对象
    }
}
