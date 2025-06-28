package com.sailfish.server.command.executor;

import com.sailfish.server.ProtocolContext;
import com.sailfish.server.command.domain.YkcDownlinkMsg;
import com.sailfish.server.command.domain.YkcUplinkMsg;
import com.sailfish.server.protocol.TcpSession;
import lombok.extern.slf4j.Slf4j;

/**
 * 云快充上行命令执行器
 *
 * @author wangpeixin
 * @since 2025/6/26 13:00
 */
@Slf4j
public abstract class AbstractYkcUplinkCmdExe implements ICmdExecutor<YkcUplinkMsg> {

    // TODO:
    protected void buildQueueMsg(){
        log.info("构建上行消息的proto消息");
    }

    // 下行回复
    protected abstract void sendDownlinkMsg(TcpSession session, YkcDownlinkMsg msg);
}
