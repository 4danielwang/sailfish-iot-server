package com.sailfish.server.handler;

import com.sailfish.server.net.TcpUplinkMsg;
import com.sailfish.server.protocol.TcpSession;
import com.sailfish.server.protocol.enums.SessionCloseReason;
import com.sailfish.server.protocol.ykc150.ProtocolMsgProcessor;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 处理协议消息
 * 处理上行和下行
 * channel操作、统计、日志等
 *
 * @author wangpeixin
 * @since 2025/6/24 11:38
 */
@Data
@Slf4j
public class UplinkHandler extends SimpleChannelInboundHandler<TcpUplinkMsg> {

    // TODO: 增加状态统计

    // 协议消息处理器
    private final ProtocolMsgProcessor processor;

    private final TcpSession session;

    public UplinkHandler(ProtocolMsgProcessor processor) {
        this.processor = processor;
        this.session = new TcpSession(this::writeAndFlush); // 创建session
    }

    // 处理上行消息
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TcpUplinkMsg msg) throws Exception {

        if (log.isDebugEnabled()) {
            log.debug("{}{} Netty拆出上行报文:{}",ctx.channel(), session, msg);
        }
        // 更新session时间
        session.setLastActivityTime(LocalDateTime.now());

        if(session.getClientAddr() == null){
            session.setClientAddr(ctx.channel().remoteAddress());
        }

        // 会话绑定
        if(session.getCtx() == null){
            session.setCtx(ctx);
        }

        // 获取数据
//        byte[] data = msg.data();

        try{
            process(msg);
        }catch (Exception e){
            log.error("{}{} TCP管道处理报文异常", ctx.channel(), session, e);
        }


    }

    // 转发给处理器
    private void process(TcpUplinkMsg msg){
        log.info("【UplinkHandler】处理上行消息: {}", msg);
        processor.handleUplink(msg, session);
    }

    // 写数据到channel
    protected void writeAndFlush(ByteBuf... list){
        if (list == null || list.length == 0) {
            return;
        }

        ChannelHandlerContext ctx = session.getCtx();
        if(ctx.isRemoved()){
            session.doClose(SessionCloseReason.INACTIVE);

            log.warn("{}{} TCP会话已失效，因此删除会话", ctx.channel(), session);

            return;
        }

        for (ByteBuf buf : list) {
          try {
              if (Objects.isNull(buf)) {
                  log.warn("{}{} 下发空报文被拦截", ctx.channel(), session);
                  continue;
              }

              ctx.writeAndFlush(Unpooled.wrappedBuffer(buf))
                      .addListener((ChannelFutureListener) channelFuture -> {
                            if(channelFuture.isDone() && !channelFuture.isSuccess()){
                                log.info("{} 下行报文发送未成功", session);
                            }
                      });
          } catch (Exception e){
                log.error("{}{} 写入到通道异常", ctx.channel(), session, e);

                throw e;
          }
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);

        log.info("{} 通道活跃", ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);

        log.info("{}{} 通道不活跃", ctx.channel(), session);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);

        log.info("{} 打开通道", ctx.channel());
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);

        log.info("{}{} 关闭通道", ctx.channel(), session);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
        ctx.flush();

        log.trace("{}{} 通道读取完成", ctx.channel(), session);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("{}{} Invalid message received, Exception caught", ctx.channel(), session, cause);

    }
}
