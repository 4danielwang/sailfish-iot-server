package com.sailfish.server.core.handler;

import com.sailfish.server.core.enums.PacketType;
import com.sailfish.server.core.protocol.MessageProcessor;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class ChannelInitializerFactory {

    /**
     * 创建channel pipeline
     * @param protocolName 协议名称
     * @param processor 消息处理器
     * @param type 报文数据格式类型
     * @author wangpeixin
     * @since 2025/7/9 16:42
     */
    public static ChannelInitializer<SocketChannel> createInitializer(String protocolName, PacketType type, MessageProcessor processor) {

        final int IDLE_READ_TIMEOUT_ONE_HOUR_SECONDS = 3600; // 空闲超时时间，单位为秒

        return switch (type){
            case BINARY -> new ChannelInitializer<>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline()
                            .addLast(new ConnectionLimitHandler(protocolName, 10000)) // 限制最大连接数为
                            .addLast(new IdleStateHandler(IDLE_READ_TIMEOUT_ONE_HOUR_SECONDS, 0, 0, TimeUnit.SECONDS)) // 空闲检测
                            .addLast(new IdleEventHandler()) // 空闲检测
                            .addLast(new LengthFieldBasedFrameDecoder(256, 1, 1, 2, 0)) // 拆包粘包
                            .addLast(new DownlinkMessageEncoder(protocolName, processor))// 编码器
                            .addLast(new ProtocolDecoder<>(protocolName, ProtocolDecoder::toByteArray)) // 解码器
                            .addLast(new UplinkMessageDecoder<>(protocolName, processor)); // 解码器
                }
            };
            case null, default -> throw new IllegalArgumentException("Unsupported PacketType :" + type);
        };

    }
}
