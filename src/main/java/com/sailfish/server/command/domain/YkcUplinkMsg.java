package com.sailfish.server.command.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.UUID;

/**
 * 上行指令消息对象
 * 由MessageProcessor创建给CmdExecutor使用
 *
 * @author wangpeixin
 * @since 2025/6/24 16:58
 */
@Data
@Accessors(chain = true)
public class YkcUplinkMsg implements Serializable {
    // 消息ID
    private final UUID id;

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
    private byte[] msgBody;

    // 校验和
    private int checkSum;

    // 真实报文
    private byte[] rawFrame;

    public YkcUplinkMsg(UUID id) {
        this.id = id;
    }

    public YkcUplinkMsg() {
        this(UUID.randomUUID());
    }
}
