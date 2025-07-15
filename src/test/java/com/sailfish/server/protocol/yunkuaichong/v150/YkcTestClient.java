package com.sailfish.server.protocol.yunkuaichong.v150;

/**
 * 测试
 *
 * @author wangpeixin
 * @since 2025/7/10 16:21
 */
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class YkcTestClient {

    private static final Logger log = LoggerFactory.getLogger(YkcTestClient.class);
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 8080;
    private static final byte[] MESSAGE_X01 = {
            (byte) 0x68, (byte) 0x22, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x55, (byte) 0x03,
            (byte) 0x14, (byte) 0x12, (byte) 0x78, (byte) 0x23, (byte) 0x05, (byte) 0x00, (byte) 0x02, (byte) 0x0A,
            (byte) 0x56, (byte) 0x34, (byte) 0x2E, (byte) 0x31, (byte) 0x2E, (byte) 0x35, (byte) 0x30, (byte) 0x00,
            (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01,
            (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x04, (byte) 0x67, (byte) 0x5A
    };
    private static final byte[] MESSAGE_X03 = {
            (byte) 0x68, (byte) 0x0D, (byte) 0x00, (byte) 0x01, (byte) 0x00,
            (byte) 0x03, (byte) 0x32, (byte) 0x01, (byte) 0x02, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x01, (byte) 0x00,
            (byte) 0xF1, (byte) 0x1A
    };
    private static final byte[] MESSAGE_X05 = {
            (byte) 0x68, (byte) 0x0D, (byte) 0x00, (byte) 0x02, (byte) 0x00,
            (byte) 0x05, (byte) 0x32, (byte) 0x01, (byte) 0x02, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x01,
            (byte) 0xD5, (byte) 0x51
    };
    private static final byte[] MESSAGE_X09 = {
            (byte) 0x68, (byte) 0x0B, (byte) 0x00, (byte) 0x01, (byte) 0x00,
            (byte) 0x09, (byte) 0x32, (byte) 0x01, (byte) 0x02, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x3D, (byte) 0x59
    };
    private static final byte[] MESSAGE_X13 = {
            (byte) 0x68, (byte) 0x40, (byte) 0x1A, (byte) 0x03, (byte) 0x00,
            (byte) 0x13, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x55, (byte) 0x03, (byte) 0x14,
            (byte) 0x12, (byte) 0x78, (byte) 0x23, (byte) 0x05, (byte) 0x02,
            (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x01, (byte) 0x02,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0xE2, (byte) 0x52
    };

    public static void main(String[] args) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            // 参考服务器配置，添加 LengthFieldBasedFrameDecoder 进行拆包粘包处理
                            // maxFrameLength: 256, lengthFieldOffset: 1, lengthFieldLength: 1, lengthAdjustment: 2, initialBytesToStrip: 0
                            p.addLast(new LengthFieldBasedFrameDecoder(256, 1, 1, 2, 0));
                            p.addLast(new YkcClientHandler());
                        }
                    });

            ChannelFuture f = b.connect(HOST, PORT).sync();
            log.info("客户端已连接到 {}:{}", HOST, PORT);
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }

    private static class YkcClientHandler extends ChannelInboundHandlerAdapter {
        private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        @Override
        public void channelActive(ChannelHandlerContext ctx) {
            log.info("通道已激活，准备定时发送消息...");
            scheduler.scheduleAtFixedRate(() -> {
                log.info("发送报文 : {}", ByteBufUtil.hexDump(MESSAGE_X01));
                ctx.writeAndFlush(Unpooled.wrappedBuffer(MESSAGE_X01));
            }, 5, 10, TimeUnit.SECONDS);
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            ByteBuf buf = (ByteBuf) msg;
            try {
                byte[] req = new byte[buf.readableBytes()];
                buf.readBytes(req);
                log.info("收到响应: {}", ByteBufUtil.hexDump(req));
            } finally {
                buf.release();
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            log.error("处理时发生异常", cause);
            ctx.close();
            scheduler.shutdown();
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            log.warn("与服务器的连接已断开。");
            scheduler.shutdown();
            super.channelInactive(ctx);
        }
    }
}
