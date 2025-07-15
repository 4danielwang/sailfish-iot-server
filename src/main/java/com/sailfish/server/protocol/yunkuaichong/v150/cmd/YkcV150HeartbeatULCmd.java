package com.sailfish.server.protocol.yunkuaichong.v150.cmd;

import com.sailfish.server.common.annotations.CommandCode;
import com.sailfish.server.common.util.codec.BCDUtil;
import com.sailfish.server.core.protocol.ProtocolContext;
import com.sailfish.server.core.session.ProtocolSession;
import com.sailfish.server.protocol.yunkuaichong.v150.dto.YkcProcessorToUplinkExeMessage;
import com.sailfish.server.protocol.yunkuaichong.v150.enums.YkcDownCmdEnum;
import com.sailfish.server.protocol.yunkuaichong.v150.enums.YkcUpCmdEnum;
import com.sailfish.server.protocol.yunkuaichong.v150.executor.YkcCmdUpExecutor;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;

import static com.sailfish.server.common.constants.CacheConstant.CACHE_UPLINK_ACK;

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

        try{
            // 充电桩编号
            byte[] pileCodeBytes = new byte[7];
            byteBuf.readBytes(pileCodeBytes);
            String pileCode = BCDUtil.toString(pileCodeBytes);

            // 枪号
            int gunCode = byteBuf.readUnsignedByte();
            // 枪状态
            int gunState = byteBuf.readUnsignedByte();

            byte[] ack = pingAck(session, message.getSequenceNumber(), message.getEncryptionFlag(), pileCodeBytes, gunCode);
            // 缓存下行回复消息
            session.getRequestCache().put(String.format(CACHE_UPLINK_ACK, message.getSequenceNumber(), YkcUpCmdEnum.HEARTBEAT.getShortCode()), ack);
        }finally {
            byteBuf.release();
            log.debug("{} 云快充1.5.0充电桩心跳包处理完成", session);
        }

    }

    // TODO: 心跳直接回复
    private byte[] pingAck(ProtocolSession session, int seqNo, int encryptFlag ,byte[] pileCodeBytes, int gunCode) {
        log.debug("{} 云快充1.5.0心跳应答: {}, seqNo: {}, encryptFlag: {}, pileCodeBytes: {}, gunCode: {}",
                session, YkcDownCmdEnum.HEARTBEAT_ACK, seqNo, encryptFlag, BCDUtil.toString(pileCodeBytes), gunCode);

        ByteBuf body = Unpooled.buffer(9);

        try{
            body.writeBytes(pileCodeBytes);
            body.writeByte(gunCode);
            body.writeByte(0); // 置0

            return encodeAndWriteFlush(YkcDownCmdEnum.HEARTBEAT_ACK,
                    seqNo,
                    encryptFlag,
                    body,
                    session);
        }finally {
            body.release();
            log.debug("{} 云快充1.5.0心跳应答: {}", session, YkcDownCmdEnum.HEARTBEAT_ACK);
        }

    }
}
