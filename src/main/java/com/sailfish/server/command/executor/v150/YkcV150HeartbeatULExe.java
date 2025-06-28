package com.sailfish.server.command.executor.v150;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sailfish.server.ProtocolContext;
import com.sailfish.server.codec.BCDUtil;
import com.sailfish.server.command.domain.YkcDownlinkMsg;
import com.sailfish.server.command.domain.YkcUplinkMsg;
import com.sailfish.server.command.executor.AbstractYkcUplinkCmdExe;
import com.sailfish.server.protocol.TcpSession;
import com.sailfish.server.serde.JacksonUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;

/**
 * 云快充1.5
 * 心跳上行执行器
 *
 * @author wangpeixin
 * @since 2025/6/26 13:37
 */
@Slf4j
public class YkcV150HeartbeatULExe extends AbstractYkcUplinkCmdExe {

    @Override
    public void execute(TcpSession session, YkcUplinkMsg msg, ProtocolContext ctx) {
        log.debug("{} 云快充1.5.0充电桩心跳包", session);
        ByteBuf byteBuf = Unpooled.wrappedBuffer(msg.getMsgBody());

        ObjectNode additionalInfo = JacksonUtil.newObjectNode();

        byte[] pileCodeBytes = new byte[7];
        byteBuf.readBytes(pileCodeBytes);
        String pileCode = BCDUtil.toString(pileCodeBytes);

        byte gunCodeByte = byteBuf.readByte();
        int gunCode = Integer.parseInt(BCDUtil.toString(gunCodeByte));
        additionalInfo.put("枪号", gunCode);

        int gunState = byteBuf.readUnsignedByte();
        additionalInfo.put("枪状态(0正常 1故障)", gunState);

        // 刷新session
        ctx.getSessionProvider().refresh(session);

        // TODO：构建心跳请求
        log.info("模拟构建心跳请求");
//        HeartBeatRequest heartBeatRequest = HeartBeatRequest.newBuilder()
//                .setPileCode(pileCode)
//                .setRemoteAddress(tcpSession.getAddress().toString())
//                .setNodeId(ctx.getServiceInfoProvider().getServiceId())
//                .setNodeHostAddress(ctx.getServiceInfoProvider().getHostAddress())
//                .setNodeRestPort(ctx.getServiceInfoProvider().getRestPort())
//                .setNodeGrpcPort(ctx.getServiceInfoProvider().getGrpcPort())
//                .setAdditionalInfo(additionalInfo.toString())
//                .build();
        // TODO： 构建queue msg

        // TODO： forwarder转发出去
        log.info("模拟发送心跳包请求到后端服务");
//        UplinkQueueMessage uplinkQueueMessage = uplinkMessageBuilder(heartBeatRequest.getPileCode(), tcpSession, yunKuaiChongUplinkMessage)
//                .setHeartBeatRequest(heartBeatRequest)
//                .build();
//        tcpSession.getForwarder().sendMessage(uplinkQueueMessage);

        // TODO：发回响应
        log.info("模拟发送心跳包响应");
//        pingAck(tcpSession, yunKuaiChongUplinkMessage, pileCodeBytes, gunCodeByte);

    }

    @Override
    protected void sendDownlinkMsg(TcpSession session, YkcDownlinkMsg msg) {

    }

//    // 发送心跳包响应
//    private void pingAck(TcpSession tcpSession, YkcUplinkMsg msg, byte[] pileCodeBytes, byte gunCodeByte) {
//        ByteBuf pingAckMsgBody = Unpooled.buffer(9);
//        pingAckMsgBody.writeBytes(pileCodeBytes);
//        pingAckMsgBody.writeByte(gunCodeByte);
//        pingAckMsgBody.writeByte(0);
//
//        YkcCmdUtils.encodeAndWriteFlush(HEARTBEAT,
//                msg.getSequenceNumber(),
//                msg.getEncryptionFlag(),
//                pingAckMsgBody,
//                tcpSession);
//    }
}

