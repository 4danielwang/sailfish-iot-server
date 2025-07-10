package com.sailfish.server.protocol.yunkuaichong.v150.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.UUID;

/**
 * 下行消息
 *
 * @author wangpeixin
 * @since 2025/7/9 15:47
 */
@Data
@Accessors(chain = true)
public class YkcProcessorToUplinkExeMessage {

    // 消息ID
    private final String id;

    // 起始域
    private int head;

    // 数据长度
    private int dataLength;

    // 序列号
    private int sequenceNumber;

    // 加密标识
    private int encryptionFlag;

    // 指令
    private int cmd;

    // 消息体
    private byte[] content;

    // 校验和
    private int checkSum;

    // 真实报文
    private byte[] rawFrame;

}
