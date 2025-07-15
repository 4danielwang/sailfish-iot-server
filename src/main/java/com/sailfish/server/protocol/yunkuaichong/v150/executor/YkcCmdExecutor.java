package com.sailfish.server.protocol.yunkuaichong.v150.executor;

import com.sailfish.server.checksum.CrcCalculator;
import com.sailfish.server.common.util.codec.BCDUtil;
import com.sailfish.server.common.util.codec.ByteUtil;
import com.sailfish.server.core.session.ProtocolSession;
import com.sailfish.server.protocol.yunkuaichong.v150.checksum.YkcCrcCalculator;
import com.sailfish.server.protocol.yunkuaichong.v150.enums.YkcDownCmdEnum;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.sailfish.server.core.session.ProtocolSession.SeqNoLength.SHORT;
import static com.sailfish.server.protocol.yunkuaichong.v150.constants.YkcConstant.YUNKUAICHONG_HEAD;
import static com.sailfish.server.protocol.yunkuaichong.v150.constants.YkcConstant.YUNKUAICHONG_NORMAL_ENCRYPTION_FLAG;

/**
 * 云快充协议抽象命令执行器
 *
 * @author wangpeixin
 * @since 2025/7/9 09:59
 */
@RequiredArgsConstructor
public abstract class YkcCmdExecutor {

    protected final CrcCalculator crcCalculator;

    public YkcCmdExecutor() {
        crcCalculator = new YkcCrcCalculator();
    }

    /**
     * 桩号编码 hex string -> BCD byte[]
     * 桩号长度7B hex string 14位
     *
     * @author wangpeixin
     * @since 2025/7/9 10:02
     */
    protected byte[] encodePileNo(String pileNo){
        if (StringUtils.length(pileNo) > 14) {
            throw new IllegalArgumentException("云快充可接受最大桩编号为14位hex");
        }

        String pileCodeStr = StringUtils.leftPad(pileNo, 14, '0');

        return BCDUtil.toBytes(pileCodeStr);
    }

    /**
     * 枪号编码 hex string -> BCD byte[]
     *
     * @author wangpeixin
     * @since 2025/7/9 10:05
     */
    protected static byte[] encodeGunCode(String gunCode) {
        if (StringUtils.length(gunCode) > 2) {
            throw new IllegalArgumentException("云快充可接受最大枪编号为2位hex");
        }

        String gunCodeStr = StringUtils.leftPad(gunCode, 2, '0');

        return BCDUtil.toBytes(gunCodeStr);
    }

    protected byte[] encode(YkcDownCmdEnum downCmd,
                            int seqNo,
                            int encryptionFlag,
                            ByteBuf msgBody) {
        int msgBodyLength = msgBody.readableBytes();
        ByteBuf response = Unpooled.buffer(msgBodyLength + 6);
        response.writeByte(YUNKUAICHONG_HEAD); // 起始标志
        response.writeByte(msgBodyLength + 4); // 数据长度=序列号域+加密标志+帧类型标志+消息体=消息体+4
        response.writeShortLE(seqNo); // 序列号域 按照小端序写
        response.writeByte(encryptionFlag); // 加密标志

        response.writeByte(downCmd.getShortCode()); // 帧标志类型
        response.writeBytes(msgBody); // 消息体

        // 帧校验域：从序列号域到数据域的 CRC 校验，校验多项式为 0x180D,低字节在前，高字节在后
        byte[] checkArr = new byte[msgBodyLength + 4];
        System.arraycopy(response.array(), 2, checkArr, 0, checkArr.length);

        response.writeShortLE(crcCalculator.calculate(checkArr)); // 校验和 按小端序写

        return ByteUtil.toBytes(response);
    }

    /**
     * 发送tcp报文到tcp连接
     *
     * @author wangpeixin
     * @since 2025/7/9 10:37
     */
    protected void encodeAndWriteFlush(YkcDownCmdEnum downlinkCmd,
                                       ByteBuf msgBody,
                                       ProtocolSession session) {

        byte[] encode = encode(downlinkCmd,
                session.nextSeqNo(SHORT),
                YUNKUAICHONG_NORMAL_ENCRYPTION_FLAG,
                msgBody);

        session.writeAndFlush(Unpooled.copiedBuffer(encode));
    }

    /**
     * 发送tcp报文到tcp连接
     * @param seqNo 序列号
     * @param encryptionFlag 加密标志
     * @return 下行输出报文
     * @author wangpeixin
     * @since 2025/7/11 13:44
     */
    protected byte[] encodeAndWriteFlush(YkcDownCmdEnum downlinkCmd,
                                       int seqNo,
                                       int encryptionFlag,
                                       ByteBuf msgBody,
                                       ProtocolSession session) {

        byte[] encode = encode(downlinkCmd,
                seqNo,
                encryptionFlag,
                msgBody);

        session.writeAndFlush(Unpooled.copiedBuffer(encode));
        return encode;
    }

    // 辅助函数：将long值按指定的放大倍数缩小为BigDecimal
    protected static BigDecimal reduceMagnification(long value, int magnification) {
        return new BigDecimal(value).divide(new BigDecimal(magnification), 4, RoundingMode.HALF_UP);
    }

    // 辅助函数：将long值按指定的放大倍数和小数位数缩小为BigDecimal
    protected static BigDecimal reduceMagnification(long value, int magnification, int scale) {
        return new BigDecimal(value).divide(new BigDecimal(magnification), scale, RoundingMode.HALF_UP);
    }

  }
