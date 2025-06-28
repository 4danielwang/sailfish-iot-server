package com.sailfish.server.net;

import io.netty.buffer.ByteBufUtil;

import java.util.UUID;

/**
 * tcp上行报文消息体
 * tcp frame -> TcpUplinkMsg
 *
 * @author wangpeixin
 * @since 2025/6/23 16:44
 */
public record TcpUplinkMsg (UUID id,
                            byte[] data){
    @Override
    public String toString() {
        // 转成十六进制字符串
        return ByteBufUtil.hexDump(data);

    }
}
