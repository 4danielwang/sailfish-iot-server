package com.sailfish.server.protocol.ykc150;

import com.sailfish.server.ProtocolContext;
import com.sailfish.server.command.domain.YkcDownlinkMsg;
import com.sailfish.server.command.domain.YkcUplinkMsg;
import com.sailfish.server.command.executor.AbstractYkcDownlinkCmdExe;
import com.sailfish.server.command.executor.AbstractYkcUplinkCmdExe;
import com.sailfish.server.command.executor.v150.YkcV150HeartbeatULExe;
import com.sailfish.server.command.executor.v150.YkcV150LoginAckDLExe;
import com.sailfish.server.command.executor.v150.YkcV150LoginULCmdExe;
import com.sailfish.server.forwarder.AbstractMsgForwarder;
import com.sailfish.server.net.TcpDownlinkMsg;
import com.sailfish.server.net.TcpUplinkMsg;
import com.sailfish.server.protocol.TcpSession;
import com.sailfish.server.utils.CrcUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static com.sailfish.server.constant.YkcConstants.YUNKUAICHONG_ENCRYPT_NO;
import static com.sailfish.server.constant.YkcConstants.YUNKUAICHONG_HEAD;

/**
 * 云快充1.5(基于tcp）协议处理器
 *
 * @author wangpeixin
 * @since 2025/6/24 16:03
 */
@Slf4j
public class YkcProtocolMsgProcessor extends ProtocolMsgProcessor {

    // 存储上行命令的Map
    private final Map<Integer, AbstractYkcUplinkCmdExe> uplinkCmdExeMap = new ConcurrentHashMap<>(){{
        // TODO：利用SPI机制初始化map
        // 心跳包请求
        put(0x03, new YkcV150HeartbeatULExe());

        // 登录请求
        put(0x01, new YkcV150LoginULCmdExe());

    }};

    private final Map<Integer, AbstractYkcDownlinkCmdExe> downlinkCmdExeMap = new ConcurrentHashMap<>(){{
        // 心跳响应
        put(0x04, new YkcV150LoginAckDLExe());
    }};

    public YkcProtocolMsgProcessor(AbstractMsgForwarder forwarder, ProtocolContext ctx) {
        super(forwarder, ctx);
    }

    @Override
    protected void handleUplink0(TcpUplinkMsg msg, TcpSession session) {

        log.debug("收到上行消息:{}", msg.toString());

//        final UUID msgId = msg.id();
        // 原始报文
        final byte[] data = msg.data();

        // ================== 前置快速失败检查 ==================
        // 检查第0个字节是否为0x68（协议头）
        // 消息体为0字节 总长度为8，检查总长度
        if (data.length < 8 || data[0] != YUNKUAICHONG_HEAD) {
            return;
        }

        ByteBuf in = Unpooled.wrappedBuffer(data);
        try {
            // ================== 协议头解析 ==================
            final int dataLength = in.getUnsignedByte(1); //读取第二个字节
            final int bodyLength = dataLength - 4; // 数据长度域=序列号域+加密标志+帧类型标志+消息体（消息体+4）
            final int checksumPos = 6 + bodyLength; // crc起始位置 除去crc的内容长度

            // ================== 组合边界检查 ==================
            /**
             * dataLength 必须大于等于4（序列号2字节 + 加密标志1字节 + 帧类型1字节 + 消息体N字节）
             * 可读数据总长度必须大于等于 checksumPos + 2（校验和2字节）
             */
            if (dataLength < 4 || in.readableBytes() < checksumPos + 2) {
                return;
            }

            // ================== 字段快速解析 ==================
            final int seqNo = in.getUnsignedShort(2);
            final int encryptFlag = in.getUnsignedByte(4);
            final int frameType = in.getUnsignedByte(5);

            // ================== 校验和双模式处理 ==================
            // 兼容小端/大端不同的设备/协议
            final int checkSumLE = in.getUnsignedShortLE(checksumPos);
            final int checkSumBE = in.getUnsignedShort(checksumPos);

            // ================== 校验数据() ==================
            final byte[] checkData = Arrays.copyOfRange(data,  2, 2 + dataLength);

            // ================== 短路校验流程 ==================
            Pair<Boolean, Integer> checkResult = CrcUtil.checkCrc(checkData, checkSumLE);
            if (!checkResult.getLeft()) {
                checkResult = CrcUtil.checkCrc(checkData, checkSumBE);
                log.debug("云快充校验域一次校验失败 CMD:{} 校验和：0x{} 期望校验和:0x{}",
                        frameType, Integer.toHexString(checkSumBE), Integer.toHexString(checkResult.getRight()));
            }

            // ================== 最终校验失败处理 ==================
            if (!checkResult.getLeft()) {
                log.debug("云快充校验域二次校验失败 CMD:{} 校验和：0x{} 期望校验和:0x{}",
                        frameType, Integer.toHexString(checkSumBE), Integer.toHexString(checkResult.getRight()));
                return;
            }
            log.debug("校验成功 CMD:{} 校验和：0x{} 期望校验和:0x{}",
                    frameType, Integer.toHexString(checkSumBE), Integer.toHexString(checkResult.getRight()));

            // ================== 消息对象智能构建 ==================
            // 取出消息体
            ByteBuf slicedBuf = in.slice(6, bodyLength);

            // 判断消息体长度是否与数据长度推算出来的一致
            if (slicedBuf.readableBytes() != bodyLength) {
                log.error("协议体长度异常: expected={}, actual={}",
                        bodyLength, slicedBuf.readableBytes());
                return;
            }

            byte[] msgBody = new byte[bodyLength];
            slicedBuf.readBytes(msgBody);

            // 发送上行指令
            exeUplinkCmd(new YkcUplinkMsg()
                            .setHead(YUNKUAICHONG_HEAD)
                            .setDataLength(dataLength)
                            .setSequenceNumber(seqNo)
                            .setEncryptionFlag(YUNKUAICHONG_ENCRYPT_NO)
                            .setCmd(frameType)
                            .setMsgBody(msgBody)
                            .setCheckSum(checkResult.getRight())
                            .setRawFrame(data),
                    session);

        } finally {
            in.release();
        }
    }

    @Override
    protected void handleDownlink0(TcpDownlinkMsg msg, TcpSession session) {
        log.info("收到下行消息:{}", msg.toString());

        final UUID id = msg.id();
        final byte[] data = msg.data();

        // 解析命令
        // TODO：构造和发送下行命令
    }


    // 执行命令
    private void exeUplinkCmd(YkcUplinkMsg msg, TcpSession session) {
        AbstractYkcUplinkCmdExe exe = uplinkCmdExeMap.get(msg.getCmd());

        if (exe == null) {
            log.info("{} 云快充协议接收到未知的上行指令 {}", session, msg.getCmd());
            return;
        }

        exe.execute(session, msg, ctx);
    }

    // 执行下行命令
    private void exeDownlinkCmd(YkcDownlinkMsg msg, TcpSession session){
        AbstractYkcDownlinkCmdExe exe = downlinkCmdExeMap.get(msg.getCmd());

        if (exe == null) {
            log.info("云快充协议接收到未知的下行指令 {}", msg.getCmd());
            return;
        }
        exe.execute(session, msg, ctx);
    }


}
