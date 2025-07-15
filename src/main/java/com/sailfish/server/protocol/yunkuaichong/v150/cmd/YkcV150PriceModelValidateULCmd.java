package com.sailfish.server.protocol.yunkuaichong.v150.cmd;

import com.sailfish.server.common.annotations.CommandCode;
import com.sailfish.server.core.protocol.ProtocolContext;
import com.sailfish.server.core.session.ProtocolSession;
import com.sailfish.server.protocol.yunkuaichong.v150.constants.YkcConstant;
import com.sailfish.server.protocol.yunkuaichong.v150.dto.YkcProcessorToUplinkExeMessage;
import com.sailfish.server.protocol.yunkuaichong.v150.enums.YkcDownCmdEnum;
import com.sailfish.server.protocol.yunkuaichong.v150.enums.YkcUpCmdEnum;
import com.sailfish.server.protocol.yunkuaichong.v150.executor.YkcCmdUpExecutor;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;

import static com.sailfish.server.common.constants.CacheConstant.CACHE_UPLINK_ACK;

/**
 * 计费模型验证请求
 *
 * @author wangpeixin
 * @since 2025/7/9 16:33
 */
@CommandCode(value = 0x05, desc = "计费模型验证请求")
@Slf4j
public class YkcV150PriceModelValidateULCmd extends YkcCmdUpExecutor {
    @Override
    public void execute(YkcProcessorToUplinkExeMessage message, ProtocolSession session, ProtocolContext ctx) {
        log.debug("{} 云快充1.5.0计费模型验证请求", session);

        ByteBuf byteBuf = Unpooled.wrappedBuffer(message.getContent());
        try{
            // 充电桩编号
            byte[] pileCodes = new byte[7];
            byteBuf.readBytes(pileCodes);
            // 计费模型编号
            int modelNo = byteBuf.readShortLE();

            byte[] ack = pingAck(pileCodes, modelNo, message.getSequenceNumber(), message.getEncryptionFlag(), session);
            // 缓存下行回复消息
            session.getRequestCache().put(String.format(CACHE_UPLINK_ACK, message.getSequenceNumber(), YkcUpCmdEnum.PRICE_MODEL.getShortCode()), ack);
        }finally {
            byteBuf.release();
            log.debug("{} 云快充1.5.0计费模型验证请求处理完成", session);
        }

    }

    // 测试回复消息
    private byte[] pingAck(byte[] pileCodes, int modelNo, int seqNo, int encryptFlag, ProtocolSession session) {
        log.debug("{} 云快充1.5.0计费模型验证应答: {}, seqNo: {}, encryptFlag: {}, pileCodes: {}, modelNo: {}",
                session, YkcDownCmdEnum.MODEL_VALIDATE_ACK, seqNo, encryptFlag, pileCodes, modelNo);
        ByteBuf body = Unpooled.buffer(9);
        try{
            body.writeBytes(pileCodes);
            body.writeShortLE(modelNo);
            body.writeByte(YkcConstant.YUNKUAICHONG_PRICE_VALIDATE_SUCCESS); // 成功标志


            byte[] ack = encodeAndWriteFlush(YkcDownCmdEnum.MODEL_VALIDATE_ACK,
                    seqNo,
                    encryptFlag,
                    body,
                    session
            );
            // 更新序列号
            session.nextSeqNo(ProtocolSession.SeqNoLength.SHORT);
            return ack;
        }finally {
            body.release();
            log.debug("{} 云快充1.5.0计费模型验证应答: {}", session, YkcDownCmdEnum.MODEL_VALIDATE_ACK);
        }

    }

}
