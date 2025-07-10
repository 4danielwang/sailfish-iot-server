package com.sailfish.server.protocol.yunkuaichong.v150.cmd;

import com.sailfish.server.common.annotations.CommandCode;
import com.sailfish.server.common.util.codec.BCDUtil;
import com.sailfish.server.core.protocol.ProtocolContext;
import com.sailfish.server.core.session.ProtocolSession;
import com.sailfish.server.protocol.yunkuaichong.v150.dto.YkcProcessorToUplinkExeMessage;
import com.sailfish.server.protocol.yunkuaichong.v150.enums.YkcDownCmdEnum;
import com.sailfish.server.protocol.yunkuaichong.v150.executor.YkcCmdUpExecutor;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;

/**
 * 云快充1.5 充电桩心跳
 *
 * @author wangpeixin
 * @since 2025/7/10 10:13
 */
@CommandCode(0x03)
@Slf4j
public class YkcV150HeartbeatULCmd extends YkcCmdUpExecutor {
    @Override
    public void execute(YkcProcessorToUplinkExeMessage message, ProtocolSession session, ProtocolContext ctx) {
        log.debug("{} 云快充1.5.0充电桩心跳包", session);
        ByteBuf byteBuf = Unpooled.wrappedBuffer(message.getContent());

        // 充电桩编号
        byte[] pileCodeBytes = new byte[7];
        byteBuf.readBytes(pileCodeBytes);
        String pileCode = BCDUtil.toString(pileCodeBytes);

        // 枪号
        int gunCode = byteBuf.readUnsignedByte();
        // 枪状态
        int gunState = byteBuf.readUnsignedByte();

        pingAck(session, pileCodeBytes);
    }

    // TODO: 心跳直接回复
    private void pingAck(ProtocolSession session, byte[] pileCodeBytes) {
        ByteBuf body = Unpooled.buffer(9);
        body.writeBytes(pileCodeBytes);

        encodeAndWriteFlush(YkcDownCmdEnum.HEARTBEAT_ACK,
                body,
                session);
    }
}
