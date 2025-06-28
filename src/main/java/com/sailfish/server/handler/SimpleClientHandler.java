package com.sailfish.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 客户端的入站处理器
 * 处理服务器到客户端的消息
 *
 * @author wangpeixin
 * @since 2025/6/23 09:06
 */
public class SimpleClientHandler extends SimpleChannelInboundHandler<String> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String s) throws Exception {
        System.out.println("服务器端返回的数据:" + s);
    }
}
