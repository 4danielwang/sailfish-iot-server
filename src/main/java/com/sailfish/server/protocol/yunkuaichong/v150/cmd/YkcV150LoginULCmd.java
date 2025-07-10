package com.sailfish.server.protocol.yunkuaichong.v150.cmd;

import com.sailfish.server.common.annotations.CommandCode;
import com.sailfish.server.common.util.codec.BCDUtil;
import com.sailfish.server.core.protocol.ProtocolContext;
import com.sailfish.server.core.session.ProtocolSession;
import com.sailfish.server.protocol.yunkuaichong.v150.constants.YkcConstant;
import com.sailfish.server.protocol.yunkuaichong.v150.enums.YkcDownCmdEnum;
import com.sailfish.server.protocol.yunkuaichong.v150.executor.YkcCmdUpExecutor;
import com.sailfish.server.protocol.yunkuaichong.v150.dto.YkcProcessorToUplinkExeMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

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

        pingAck(pileCodeBytes, session);
    }

    // 测试回复消息
    private void pingAck(byte[] pileCodeBytes, ProtocolSession session) {
        ByteBuf body = Unpooled.buffer(8);
        body.writeBytes(pileCodeBytes);
        body.writeByte(YkcConstant.YUNKUAICHONG_ACK_SUCCESS);

        encodeAndWriteFlush(YkcDownCmdEnum.LOGIN_ACK,
                body,
                session
                );
    }

}
