package com.sailfish.server.protocol.yunkuaichong.v150.executor;

import cn.hutool.core.util.ClassUtil;
import com.sailfish.server.checksum.CrcCalculator;
import com.sailfish.server.common.annotations.CommandCode;
import com.sailfish.server.core.broker.Forwarder;
import com.sailfish.server.core.dto.DecoderToProcessorMessage;
import com.sailfish.server.core.dto.EncoderToProcessorMessage;
import com.sailfish.server.core.dto.req.DownlinkMessageReq;
import com.sailfish.server.core.protocol.MessageProcessor;
import com.sailfish.server.core.protocol.ProtocolContext;
import com.sailfish.server.core.session.ProtocolSession;
import com.sailfish.server.protocol.yunkuaichong.v150.checksum.YkcCrcCalculator;
import com.sailfish.server.protocol.yunkuaichong.v150.dto.YkcProcessorToDownlinkExeMessage;
import com.sailfish.server.protocol.yunkuaichong.v150.dto.YkcProcessorToUplinkExeMessage;
import com.sailfish.server.protocol.yunkuaichong.v150.enums.YkcDownCmdEnum;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.sailfish.server.common.constants.CacheConstant.CACHE_UPLINK_ACK;
import static com.sailfish.server.protocol.yunkuaichong.v150.constants.YkcConstant.YUNKUAICHONG_HEAD;

/**
 * 云快充消息处理器
 *
 * @author wangpeixin
 * @since 2025/7/9 08:55
 */
@Slf4j
public class YkcMessageProcessor extends MessageProcessor {

    private static final Map<Integer, YkcCmdDownExecutor> cmdDownMap = new ConcurrentHashMap<>();
    private static final Map<Integer, YkcCmdUpExecutor> cmdUpmap = new ConcurrentHashMap<>();

    private CrcCalculator calculator;

    public YkcMessageProcessor(Forwarder forwarder, ProtocolContext protocolContext) {
        super(forwarder, protocolContext);
        this.calculator = new YkcCrcCalculator();

        // 扫描带有 CommandCode 注解的类
        Set<Class<?>> cmdClasses = ClassUtil.scanPackageByAnnotation("com.sailfish.server.protocol.yunkuaichong.v150.cmd", CommandCode.class);
        cmdClasses.stream().filter(YkcCmdDownExecutor.class::isAssignableFrom)
                .forEach(clazz -> {
                    // 取到注解中的命令值（帧类型码）
                    int cmd = clazz.getAnnotation(CommandCode.class).value();
                    try {
                        YkcCmdDownExecutor yunKuaiChongUplinkCmdExe = (YkcCmdDownExecutor) clazz.getDeclaredConstructor().newInstance();
                        cmdDownMap.put(cmd, yunKuaiChongUplinkCmdExe);
                    } catch (InstantiationException |
                             IllegalAccessException |
                             InvocationTargetException |
                             NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                });

        cmdClasses.stream().filter(YkcCmdUpExecutor.class::isAssignableFrom)
                .forEach(clazz -> {
                    int cmd = clazz.getAnnotation(CommandCode.class).value();
                    try {
                        YkcCmdUpExecutor yunKuaiChongDownlinkCmdExe = (YkcCmdUpExecutor) clazz.getDeclaredConstructor().newInstance();
                        cmdUpmap.put(cmd, yunKuaiChongDownlinkCmdExe);
                    } catch (InstantiationException |
                             IllegalAccessException |
                             InvocationTargetException |
                             NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    /**
     * 处理上行消息
     *
     * @author wangpeixin
     * @since 2025/7/9 15:29
     */
    @Override
    protected void uplink0(DecoderToProcessorMessage decoderToProcessorMessage) {

        final String msgId = decoderToProcessorMessage.id();
        final byte[] msg = decoderToProcessorMessage.content();
        final ProtocolSession session = decoderToProcessorMessage.session();

        // ================== 前置快速失败检查 ==================
        // 检查第0个字节是否为0x68（协议头）
        // 消息体为0字节 总长度为8，检查总长度
        if (msg.length < 8 || msg[0] != 0x68) {
            return;
        }

        // 手动创建的ByteBuf需要释放
        ByteBuf in = Unpooled.wrappedBuffer(msg);
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
            final int seqNo = in.getUnsignedShortLE(2);
            final int encryptFlag = in.getUnsignedByte(4);
            final int frameType = in.getUnsignedByte(5);

            // ==================快速失败 如果历史消息且ack不在缓存 ==================
            String cacheKey = String.format(CACHE_UPLINK_ACK, seqNo, frameType);
            if(seqNo < session.getSeqNo().get()){
                // 幂等处理 重复请求
                if(session.getRequestCache().getIfPresent(cacheKey) != null) {
                    log.info("{} 云快充协议幂等处理，重复请求 CMD:{} 指令序列号:{}", session, frameType, seqNo);
                    byte[] ack = (byte[]) session.getRequestCache().getIfPresent(cacheKey);
                    session.writeAndFlush(Unpooled.wrappedBuffer(ack));
                    return ;
                }
                // 无效数据 缓存已过期
                log.info("{} 云快充协议接收到过期的上行指令 CMD:{} 指令序列号:{}", session, frameType, seqNo);
                return ;
            }else if(seqNo > session.getSeqNo().get()){
                log.error("{} 云快充协议接收到的乱序的指令 CMD:{} 指令序列号:{} 服务器序列号:{}", session, frameType, seqNo, session.getSeqNo().get());
                return ;
            }

            // ================== 校验和双模式处理 ==================
            // 兼容小端/大端不同的设备/协议
            final int checkSumLE = in.getUnsignedShortLE(checksumPos);
            final int checkSumBE = in.getUnsignedShort(checksumPos);

            // ================== 校验数据智能拷贝 ==================
            final byte[] checkData = Arrays.copyOfRange(msg, 2, 2 + dataLength);

            // ================== 短路校验流程 ==================
            Pair<Boolean, Integer> checkResult = calculator.validate(checkData, checkSumLE);
            if (!checkResult.getLeft()) {
                checkResult = calculator.validate(checkData, checkSumBE);
                if (log.isDebugEnabled()) { // 日志惰性计算
                    log.info("{} 云快充校验域一次校验失败 CMD:{} 校验和：0x{} 期望校验和:0x{}",
                            session, frameType, Integer.toHexString(checkSumBE), Integer.toHexString(checkResult.getRight()));
                }
            }

            // ================== 最终校验失败处理 ==================
            if (!checkResult.getLeft()) {
                log.info("{} 云快充校验域二次校验失败 CMD:{} 校验和：0x{} 期望校验和:0x{}",
                        session, frameType, Integer.toHexString(checkSumBE), Integer.toHexString(checkResult.getRight()));
                return;
            }

            log.info("{} 云快充校验域校验成功 CMD:{} 校验和：0x{} 期望校验和:0x{}",
                    session, frameType, Integer.toHexString(checkSumBE), Integer.toHexString(checkResult.getRight()));

            // ================== 消息对象智能构建 ==================
            ByteBuf slicedBuf = in.slice(6, bodyLength);

            // 判断消息体长度是否与数据长度推算出来的一致
            if (slicedBuf.readableBytes() != bodyLength) {
                log.error("协议体长度异常: expected={}, actual={}",
                        bodyLength, slicedBuf.readableBytes());
                return;
            }

            byte[] msgBody = new byte[bodyLength];
            slicedBuf.readBytes(msgBody);


            // 发送指令
            exeUpCmd(new YkcProcessorToUplinkExeMessage(msgId)
                    .setHead(YUNKUAICHONG_HEAD)
                    .setDataLength(dataLength)
                    .setSequenceNumber(seqNo)
                    .setEncryptionFlag(encryptFlag)
                    .setCmd(frameType)
                    .setContent(msgBody)
                    .setCheckSum(checkResult.getRight())
                    .setRawFrame(msg), session);
        } finally {
            in.release();
        }
    }


    /**
     * 处理下行消息
     *
     * @author wangpeixin
     * @since 2025/7/9 15:29
     */
    @Override
    protected void downlink0(EncoderToProcessorMessage encoderToProcessorMessage) {
        ProtocolSession session = encoderToProcessorMessage.session();
        DownlinkMessageReq downlinkMessageReq = encoderToProcessorMessage.msg();

        YkcDownCmdEnum cmd = YkcDownCmdEnum.of(downlinkMessageReq.cmd());

        // 如果是响应下行 这里可能是上行消息的id
        exeDownCmd(new YkcProcessorToDownlinkExeMessage(encoderToProcessorMessage.id())
                .setCmd(cmd.getShortCode()), session);
    }

    // 执行上行命令
    private void exeUpCmd(YkcProcessorToUplinkExeMessage message, ProtocolSession session) {
        YkcCmdUpExecutor uplinkCmdExe = cmdUpmap.get(message.getCmd());

        if (uplinkCmdExe == null) {

            log.info("{} 云快充协议接收到未知的上行指令 0x{}", session, Integer.toHexString(message.getCmd()));

            return;
        }

        // todo 缓存
        uplinkCmdExe.execute(message, session, protocolContext);
    }

    // 执行下行命令
    private void exeDownCmd(YkcProcessorToDownlinkExeMessage message, ProtocolSession session) {
        YkcCmdDownExecutor downlinkCmdExe = cmdDownMap.get(message.getCmd());

        if (downlinkCmdExe == null) {

            log.info("{} 云快充协议接收到未知的下行指令 0x{}", session, Integer.toHexString(message.getCmd()));

            return;
        }

        downlinkCmdExe.execute(message, session);
    }

}
