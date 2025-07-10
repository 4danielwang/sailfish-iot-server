package com.sailfish.server.core.dto;

import java.net.SocketAddress;

/**
 * 协议层上行消息(经过Decoder过后的消息体)
 *
 * @param <T> 消息内容类型
 * @param ip 客户端IP地址
 * @param id 消息唯一标识符
 * @param content 消息内容
 * @param size 消息内容大小
 * @author wangpeixin
 * @since 2025/7/8 10:38
 */
public record ProtocolUplinkMessage<T>(SocketAddress ip, String id, T content, long size){

}
