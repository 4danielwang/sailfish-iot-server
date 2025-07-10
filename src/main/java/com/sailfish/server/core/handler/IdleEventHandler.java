package com.sailfish.server.core.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * 处理IdleStateEvent事件，在连接空闲时关闭连接
 */
@Slf4j
@ChannelHandler.Sharable
public class IdleEventHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            switch (e.state()) {
                case READER_IDLE:
                    log.info("[Idle] Reader idle for channel {}, closing it.", ctx.channel().remoteAddress());
                    ctx.close();
                    break;
                case WRITER_IDLE:
                    // 通常写空闲不需要服务器端主动断开
                    // log.info("[Idle] Writer idle for channel {}", ctx.channel().remoteAddress());
                    break;
                case ALL_IDLE:
                    // log.info("[Idle] All idle for channel {}", ctx.channel().remoteAddress());
                    break;
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
