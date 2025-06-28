package com.sailfish.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 服务器入站处理器
 * 处理客户端数据 执行回显
 * @author wangpeixin
 * @since 2025/6/23 08:46
 */
public class SimpleServerHandler extends SimpleChannelInboundHandler<String> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
        // 这里可以处理接收到的字符串消息
        System.out.println("Received message: " + s);
        // 可以选择回显消息
        channelHandlerContext.writeAndFlush("Echo: " + s + "\n");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
