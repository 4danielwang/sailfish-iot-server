package com.sailfish.server.command.executor.v150;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sailfish.server.ProtocolContext;
import com.sailfish.server.codec.BCDUtil;
import com.sailfish.server.command.domain.YkcDownlinkMsg;
import com.sailfish.server.command.domain.YkcUplinkMsg;
import com.sailfish.server.command.executor.AbstractYkcUplinkCmdExe;
import com.sailfish.server.dto.LoginULReq;
import com.sailfish.server.protocol.TcpSession;
import com.sailfish.server.serde.JacksonUtil;
import com.sailfish.server.utils.YkcCmdUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static com.sailfish.server.protocol.enums.YkcDownlinkCmdEnum.LOGIN_ACK;

/**
 * 云快充1.5登录认证上行处理器
 *
 * @author wangpeixin
 * @since 2025/6/26 14:00
 */
@Slf4j
public class YkcV150LoginULCmdExe extends AbstractYkcUplinkCmdExe {

    // 测试回复
//    private void testLoginAck(TcpSession session, YkcUplinkMsg msg, byte[] pileCode) {
//        ByteBuf ackBody = Unpooled.buffer(9);
//        ackBody.writeBytes(pileCode);
//        ackBody.writeByte(YUNKUAICHONG_LOGIN_ACK_SUCCESS);
//
//        YkcCmdUtils.encodeAndWriteFlush(LOGIN_ACK,
//                msg.getSequenceNumber(),
//                msg.getEncryptionFlag(),
//                ackBody,
//                session);
//    }

//    @Override
//    protected void sendDownlinkMsg(TcpSession session, YkcUplinkMsg uplinkMsg) {
//        // 构建下行消息
//        new YkcDownlinkMsg();
//        // 发送下行消息
//        YkcCmdUtils.encodeAndWriteFlush(LOGIN_ACK, msg, session);
//
//
//        log.info("{} 发送登录认证响应: {}", session, msg);
//    }

    @Override
    public void execute(TcpSession session, YkcUplinkMsg msg, ProtocolContext ctx) {
        log.debug("{} 云快充1.5.0登录认证请求", session);
        ByteBuf byteBuf = Unpooled.wrappedBuffer(msg.getMsgBody());

        ObjectNode additionalInfo = JacksonUtil.newObjectNode();

        byte[] pileCodeBytes = new byte[7];
        byteBuf.readBytes(pileCodeBytes);
        String pileCode = BCDUtil.toString(pileCodeBytes);

        int pileType = byteBuf.readUnsignedByte();
        additionalInfo.put("桩类型(0直流1交流)", pileType);

        int gunsNum = byteBuf.readUnsignedByte();
        additionalInfo.put("充电枪数量", gunsNum);
        additionalInfo.put("通信协议版本", byteBuf.readUnsignedByte());
        byte[] bytes = new byte[8];
        byteBuf.readBytes(bytes);
        additionalInfo.put("程序版本", new String(bytes, StandardCharsets.US_ASCII));
        additionalInfo.put("网络链接类型", byteBuf.readUnsignedByte());

        byte[] simB = new byte[10];
        byteBuf.readBytes(simB);
        String sim = BCDUtil.toString(simB);
        additionalInfo.put("Sim卡", sim);
        additionalInfo.put("运营商", byteBuf.readUnsignedByte());

        session.addPileCode(pileCode);

        // 登录成功，注册会话到session注册中心
        ctx.getSessionProvider().register(session);

        // 转换为业务对象dto向上发送
        LoginULReq loginULReq = LoginULReq.builder()
                .pileCode(pileCode)
                .credential(pileCode)
                .remoteAddr(session.getClientAddr().toString())
                .additionalInfo(additionalInfo.toString())
                .build();

        log.debug("[YkcV150LoginULCmdExe] 登录认证请求包装DTO: {}", loginULReq.toString());

        log.info("发送登录认证响应: {}", loginULReq);

        // 往channel发送下行消息
        // TODO： 异步执行，事件驱动
        sendDownlinkMsg(session,
                new YkcDownlinkMsg()
                        .setId(UUID.randomUUID())
                        .setCmd(LOGIN_ACK.getCmd())
                        .setRequestId(msg.getId())
        );
    }

    @Override
    protected void sendDownlinkMsg(TcpSession session, YkcDownlinkMsg msg) {

    }
}
