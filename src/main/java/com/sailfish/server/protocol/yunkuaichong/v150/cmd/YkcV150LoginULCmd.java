package com.sailfish.server.protocol.yunkuaichong.v150.cmd;

import com.sailfish.server.common.annotations.CommandCode;
import com.sailfish.server.common.util.codec.BCDUtil;
import com.sailfish.server.core.protocol.ProtocolContext;
import com.sailfish.server.core.session.ProtocolSession;
import com.sailfish.server.protocol.yunkuaichong.v150.constants.YkcConstant;
import com.sailfish.server.protocol.yunkuaichong.v150.enums.YkcDownCmdEnum;
import com.sailfish.server.protocol.yunkuaichong.v150.enums.YkcUpCmdEnum;
import com.sailfish.server.protocol.yunkuaichong.v150.executor.YkcCmdUpExecutor;
import com.sailfish.server.protocol.yunkuaichong.v150.dto.YkcProcessorToUplinkExeMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;

import static com.sailfish.server.common.constants.CacheConstant.CACHE_UPLINK_ACK;

/**
 * 登录请求指令
 *
 * @author wangpeixin
 * @since 2025/7/9 16:33
 */
@CommandCode(value = 0x01, desc = "登录请求")
@Slf4j
public class YkcV150LoginULCmd extends YkcCmdUpExecutor {
    @Override
    public void execute(YkcProcessorToUplinkExeMessage message, ProtocolSession session, ProtocolContext ctx) {
        log.debug("{} 云快充1.5.0登录认证请求", session);

        ByteBuf byteBuf = Unpooled.wrappedBuffer(message.getContent());
        // 桩号
        byte[] pileCodeBytes = new byte[7];
        byteBuf.readBytes(pileCodeBytes);
        String pileCode = BCDUtil.toString(pileCodeBytes);
        // 桩类型
        int pileType = byteBuf.readUnsignedByte();
        // 充电枪数量
        int gunsNum = byteBuf.readUnsignedByte();
        // 通信协议版本
        int protocolVersion = byteBuf.readUnsignedByte();
        // 程序版本
        byte[] versionBytes = new byte[8];
        byteBuf.readBytes(versionBytes);
        String programVersion = new String(versionBytes, StandardCharsets.US_ASCII);
        // 网络链接类型
        int networkType = byteBuf.readUnsignedByte();
        // Sim卡
        byte[] simBytes = new byte[10];
        byteBuf.readBytes(simBytes);
        String simCard = BCDUtil.toString(simBytes);
        // 运营商
        int operator = byteBuf.readUnsignedByte();

        // 添加桩号到会话
        session.bindPileNo(pileCode);

        // 注册会话到会话注册中心
        ctx.getSessionManager().register(session);

        // 缓存ackbody
        byte[] ack = pingAck(pileCodeBytes, message.getSequenceNumber(), message.getEncryptionFlag(), session);

        // 缓存下行回复消息
        session.getRequestCache().put(String.format(CACHE_UPLINK_ACK, message.getSequenceNumber(), YkcUpCmdEnum.LOGIN.getShortCode()), ack);
    }

    // 测试回复消息
    private byte[] pingAck(byte[] pileCodeBytes, int seqNo, int encryptFlag, ProtocolSession session) {
        log.debug("{} 云快充1.5.0登录认证应答: {}, seqNo: {}, encryptFlag: {}, pileCodeBytes: {}",
                session, YkcDownCmdEnum.LOGIN_ACK, seqNo, encryptFlag, BCDUtil.toString(pileCodeBytes));
        ByteBuf body = Unpooled.buffer(8);
        try{
            body.writeBytes(pileCodeBytes);
            body.writeByte(YkcConstant.YUNKUAICHONG_ACK_SUCCESS);

            byte[] bytes = encodeAndWriteFlush(YkcDownCmdEnum.LOGIN_ACK,
                    seqNo,
                    encryptFlag,
                    body,
                    session
            );
            // 更新序列号
            session.nextSeqNo(ProtocolSession.SeqNoLength.SHORT);

            return bytes;
        }finally {
            body.release();
            log.debug("{} 云快充1.5.0登录认证应答: {}", session, YkcDownCmdEnum.LOGIN_ACK);
        }

    }

}
