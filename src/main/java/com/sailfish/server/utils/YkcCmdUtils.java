package com.sailfish.server.utils;

import cn.hutool.core.text.CharSequenceUtil;
import com.sailfish.server.codec.BCDUtil;
import com.sailfish.server.protocol.TcpSession;
import com.sailfish.server.protocol.enums.SeqNumLenEnum;
import com.sailfish.server.protocol.enums.YkcDownlinkCmdEnum;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import static com.sailfish.server.codec.ByteUtil.toBytes;
import static com.sailfish.server.constant.YkcConstants.YUNKUAICHONG_ENCRYPT_NO;
import static com.sailfish.server.constant.YkcConstants.YUNKUAICHONG_HEAD;

/**
 * 云快充命令执行器
 *
 * @author wangpeixin
 * @since 2025/6/24 17:25
 */
@UtilityClass
public class YkcCmdUtils {

    public static String decodeTradeNo(byte[] tradeNo) {
        String tradeNoStr = BCDUtil.toString(tradeNo);
        return CharSequenceUtil.strip(tradeNoStr, "0", null);
    }

    public static byte[] encodePileCode(String pileCode) {
        if (StringUtils.length(pileCode) > 32) {
            throw new IllegalArgumentException("云快充1.5可接受最大桩编号为14位");
        }

        String pileCodeStr = StringUtils.leftPad(pileCode, 14, '0');

        return BCDUtil.toBytes(pileCodeStr);
    }

    public static byte[] encodeGunCode(String gunCode) {
        if (StringUtils.length(gunCode) > 2) {
            throw new IllegalArgumentException("云快充1.5可接受最大枪编号为2位");
        }

        String gunCodeStr = StringUtils.leftPad(gunCode, 2, '0');

        return BCDUtil.toBytes(gunCodeStr);
    }

    public static byte[] encodeTradeNo(String tradeNo) {
        if (StringUtils.length(tradeNo) > 32) {
            throw new IllegalArgumentException("云快充1.5可接受最大交易流水号为32位");
        }

        String tradeNoStr = StringUtils.leftPad(tradeNo, 32, '0');

        return BCDUtil.toBytes(tradeNoStr);
    }


    public byte[] encode(YkcDownlinkCmdEnum downlinkCmd,
                            int seqNo,
                            int encryptionFlag,
                            ByteBuf msgBody) {
        int msgBodyLength = msgBody.readableBytes();
        ByteBuf response = Unpooled.buffer(msgBodyLength + 6);
        // 起始标志
        response.writeByte(YUNKUAICHONG_HEAD);
        // 数据长度=序列号域+加密标志+帧类型标志+消息体=消息体+4
        response.writeByte(msgBodyLength + 4);
        // 序列号域 按照小端序写
        response.writeShortLE(seqNo);
        // 加密标志
        response.writeByte(encryptionFlag);
        // 帧标志类型
        response.writeByte(downlinkCmd.getCmd());
        // 消息体
        response.writeBytes(msgBody);

        // 帧校验域：从序列号域到数据域的 CRC 校验，校验多项式为 0x180D,低字节在前，高字节在后
        byte[] checkArr = new byte[msgBodyLength + 4];
        System.arraycopy(response.array(), 2, checkArr, 0, checkArr.length);
        // 校验和 按小端序写
        response.writeShortLE(CrcUtil.crcSum(checkArr));

        return toBytes(response);
    }

    /**
     * 编码并发送下行命令
     *
     * @param downlinkCmd 下行命令枚举
     * @param seqNo        序列号
     * @param encryptionFlag 加密标志
     * @param msgBody     消息体
     * @author wangpeixin
     * @since 2025/6/26 14:50
     */
    public void encodeAndWriteFlush(YkcDownlinkCmdEnum downlinkCmd,
                                       int seqNo,
                                       int encryptionFlag,
                                       ByteBuf msgBody,
                                       TcpSession tcpSession) {

        byte[] encode = encode(downlinkCmd, seqNo, encryptionFlag, msgBody);

        tcpSession.writeAndFlush(Unpooled.copiedBuffer(encode));
    }

    public void encodeAndWriteFlush(YkcDownlinkCmdEnum downlinkCmd,
                                       ByteBuf msgBody,
                                       TcpSession tcpSession) {

        byte[] encode = encode(downlinkCmd,
                tcpSession.nextSeqNo(SeqNumLenEnum.SHORT),
                YUNKUAICHONG_ENCRYPT_NO,
                msgBody);

        tcpSession.writeAndFlush(Unpooled.copiedBuffer(encode));
    }



}
