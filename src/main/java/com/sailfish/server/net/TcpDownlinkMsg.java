package com.sailfish.server.net;

import io.netty.buffer.ByteBufUtil;

import java.util.UUID;

/**
 * tcp下行报文体
 * TcpDownlinkMsg -> tcp frame
 *
 * @author wangpeixin
 * @since 2025/6/26 15:18
 */

public record TcpDownlinkMsg(UUID id,
                             byte[] data){
    @Override
    public String toString() {
        // 转成十六进制字符串
        return ByteBufUtil.hexDump(data);
    }
}
