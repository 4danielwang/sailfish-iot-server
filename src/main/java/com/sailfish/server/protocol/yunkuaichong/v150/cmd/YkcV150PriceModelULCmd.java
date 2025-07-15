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

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Arrays;

import static com.sailfish.server.common.constants.CacheConstant.CACHE_UPLINK_ACK;

/**
 * 计费模型请求
 *
 * @author wangpeixin
 * @since 2025/7/9 16:33
 */
@CommandCode(value = 0x09, desc = "计费模型请求")
@Slf4j
public class YkcV150PriceModelULCmd extends YkcCmdUpExecutor {
    @Override
    public void execute(YkcProcessorToUplinkExeMessage message, ProtocolSession session, ProtocolContext ctx) {
        log.debug("{} 云快充1.5.0计费模型请求", session);
        ByteBuf byteBuf = Unpooled.wrappedBuffer(message.getContent());
        try {
            // 充电桩编号
            byte[] pileCodes = new byte[7];
            byteBuf.readBytes(pileCodes);

            byte[] ack = pingAck(pileCodes, message.getSequenceNumber(), message.getEncryptionFlag(), session);
            // 缓存下行回复消息
            session.getRequestCache().put(String.format(CACHE_UPLINK_ACK, message.getSequenceNumber(), YkcUpCmdEnum.PRICE_MODEL.getShortCode()), ack);
        }finally {
            byteBuf.release();
            log.debug("{} 云快充1.5.0计费模型请求处理完成", session);
        }
    }

    // 测试回复消息
    private byte[] pingAck(byte[] pileCodes, int seqNo, int encryptFlag, ProtocolSession session) {
        log.debug("{} 云快充1.5.0计费模型应答: {}, seqNo: {}, encryptFlag: {}, pileCodes: {}",
                session, YkcDownCmdEnum.PRICE_MODEL_ACK, seqNo, encryptFlag, Arrays.toString(pileCodes));
        ByteBuf body = Unpooled.buffer(90);
        try{
            body.writeBytes(pileCodes);
            // 2字节计费模型编号
            // 小端写回为 01 00
            body.writeShortLE(0x0001);
            // 4字节电价+4字节服务费
            BigDecimal[] prices = new BigDecimal[8];
            BigDecimal accurate = new BigDecimal(10000);
            Arrays.fill(prices, new BigDecimal(1.0)); // 模拟电价和服务费为0.1元
            for (BigDecimal price : prices) {
                body.writeIntLE(price.multiply(accurate).intValue());
            }
            // 计损比例
            body.writeByte(0);
            // 时段标识，48字节
            byte[] bytes = new byte[48];
            Arrays.fill(bytes, YkcConstant.TOP_BYTE);
            body.writeBytes(bytes);

            byte[] ack = encodeAndWriteFlush(YkcDownCmdEnum.PRICE_MODEL_ACK,
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
            log.debug("{} 云快充1.5.0计费模型应答: {}", session, YkcDownCmdEnum.PRICE_MODEL_ACK);
        }

    }

}
