package com.sailfish.server.core.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 限制最大连接channel处理器
 *
 * @author wangpeixin
 * @since 2025/7/9 14:00
 */
@Slf4j
public class ConnectionLimitHandler extends ChannelInboundHandlerAdapter {

    private final String protocolName; // 协议名称

    private final AtomicInteger connections; // 连接数

    private final int maxConnections; // 最大连接数

    private final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public ConnectionLimitHandler(String protocolName, int maxConnections) {
        connections = new AtomicInteger(0);
        this.maxConnections = maxConnections;
        this.protocolName = protocolName;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        if (connections.incrementAndGet() > maxConnections) {
            ctx.close();
            log.info("[{}]{} channelRegistered超过最大连接数 {}，因此关闭连接 {}",protocolName, ctx.channel(), maxConnections, ctx.channel());
        } else {
            super.channelRegistered(ctx);
            log.info("[{}]{} channelRegistered 当前连接数 {} / {}",protocolName, ctx.channel(), connections.get(), maxConnections);
        }
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        connections.decrementAndGet();
        super.channelUnregistered(ctx);
        log.info("[{}]{} channelUnregistered 当前连接数 {} / {}",protocolName, ctx.channel(), connections.get(), maxConnections);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        channelGroup.add(ctx.channel());
        log.info("[{}]{} channelActive 当前连接数 {} / {}",protocolName, ctx.channel(), channelGroup.size(), maxConnections);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        channelGroup.remove(ctx.channel());
        log.info("[{}]{} channelInactive 当前连接数 {} / {}",protocolName, ctx.channel(), channelGroup.size(), maxConnections);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("[{}]{} ConnectionLimitHandler exceptionCaught",protocolName, ctx.channel(), cause);
        ctx.close();
    }

}
