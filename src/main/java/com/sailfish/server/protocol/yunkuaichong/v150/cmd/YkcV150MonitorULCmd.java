package com.sailfish.server.protocol.yunkuaichong.v150.cmd;

import com.sailfish.server.common.annotations.CommandCode;
import com.sailfish.server.common.util.codec.BCDUtil;
import com.sailfish.server.core.protocol.ProtocolContext;
import com.sailfish.server.core.session.ProtocolSession;
import com.sailfish.server.protocol.yunkuaichong.v150.constants.YkcConstant;
import com.sailfish.server.protocol.yunkuaichong.v150.dto.YkcProcessorToUplinkExeMessage;
import com.sailfish.server.protocol.yunkuaichong.v150.enums.YkcDownCmdEnum;
import com.sailfish.server.protocol.yunkuaichong.v150.executor.YkcCmdUpExecutor;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

/**
 * 实时监测数据上传请求
 *
 * @author wangpeixin
 * @since 2025/7/9 16:33
 */
@CommandCode(value = 0x13, desc = "实时监测数据上传请求")
@Slf4j
public class YkcV150MonitorULCmd extends YkcCmdUpExecutor {
    @Override
    public void execute(YkcProcessorToUplinkExeMessage message, ProtocolSession session, ProtocolContext ctx) {
        log.debug("{} 云快充1.5.0实时监测数据上传请求", session);

        ByteBuf byteBuf = Unpooled.wrappedBuffer(message.getContent());
        try{
            // 交易流水号
            byte[] transactionIdBytes = new byte[16];
            byteBuf.readBytes(transactionIdBytes);
            String transactionId = BCDUtil.toString(transactionIdBytes);
            // 充电桩编号
            byte[] pileCodes = new byte[7];
            byteBuf.readBytes(pileCodes);
            // 枪号
            int gunNo = byteBuf.readUnsignedByte();
            // 状态
            int status = byteBuf.readUnsignedByte();
            // 枪归位
            int gunReturn = byteBuf.readUnsignedByte();
            // 是否插枪
            int gunInsert = byteBuf.readUnsignedByte();
            // 输出电压电流
            BigDecimal outputVoltage = reduceMagnification(byteBuf.readUnsignedShortLE(), 10);
            BigDecimal outputCurrent = reduceMagnification(byteBuf.readUnsignedShortLE(), 10);
            // 9.枪线温度
            short gunLineTemperature = byteBuf.readUnsignedByte();
            // 10.枪线编码
            long gunLineCode = byteBuf.readLongLE();
            // 11.soc
            int soc = byteBuf.readUnsignedByte();
            // 12.电池组最高温度
            short maxBatteryTemperature = byteBuf.readUnsignedByte();
            // 13.累计充电时间（分钟）
            int totalChargeTime = byteBuf.readUnsignedShortLE();
            // 14.剩余时间（分钟）
            int remainMin = byteBuf.readUnsignedShortLE();
            //15.充电度数（kWh)
            BigDecimal chargeEnergy = reduceMagnification(byteBuf.readUnsignedIntLE(), 10000, 4);
            //16.计损充电度数（kWh)
            BigDecimal loseEnergy = reduceMagnification(byteBuf.readUnsignedIntLE(), 10000, 4);

            // 17.已充金额 （电费+服务费）*计损充电度数
            BigDecimal chargeAmount = reduceMagnification(byteBuf.readUnsignedIntLE(), 10000);

            // 18.硬件故障 测试发现需要使用小端计算bit, 然后对照故障表查询故障码
            byte[] warnCodeBytes = new byte[2];
            byteBuf.readBytes(warnCodeBytes);
            boolean[] warnCodes = parseFaults(warnCodeBytes);

            log.info("{} 云快充1.5.0实时监测数据上传请求: 交易流水号={}, 充电桩编号={}, 枪号={}, 状态={}, 枪归位={}, 是否插枪={}, 输出电压={}, 输出电流={}, 枪线温度={}, 枪线编码={}, soc={}, 电池组最高温度={}, 累计充电时间={}, 剩余时间={}, 充电度数={}, 计损充电度数={}, 已充金额={}, 硬件故障={}",
                    session, transactionId, BCDUtil.toString(pileCodes), gunNo, status, gunReturn, gunInsert,
                    outputVoltage, outputCurrent, gunLineTemperature, gunLineCode, soc, maxBatteryTemperature,
                    totalChargeTime, remainMin, chargeEnergy, loseEnergy, chargeAmount, warnCodes);

            pingAck(session);
        }finally {
            byteBuf.release();
            log.debug("{} 云快充1.5.0实时监测数据上传请求处理完成", session);
        }

    }

    // 占位 不返回ack
    private void pingAck(ProtocolSession session){
        // 更新序列号
        session.nextSeqNo(ProtocolSession.SeqNoLength.SHORT);
    }

    // 解析出故障原因 转为boolean数组
    public static boolean[] parseFaults(byte[] bytes) {
        // 确保输入有效
        if (bytes.length != 2) {
            throw new IllegalArgumentException("输入 byte 数组长度不为 2");
        }

        // 创建一个布尔数组来存储故障状态
        boolean[] faults = new boolean[14];

        // 读取每个比特并设置到布尔数组中
        for (int i = 0; i < 14; i++) {
            // 计算对应的字节和比特位置
            int byteIndex = i / 8; // 字节索引
            int bitIndex = i % 8;  // 比特索引

            // 使用位运算检查该比特位
            faults[i] = ((bytes[byteIndex] >> bitIndex) & 1) == 1; // 如果为 1 则故障
        }

        return faults;
    }

}
