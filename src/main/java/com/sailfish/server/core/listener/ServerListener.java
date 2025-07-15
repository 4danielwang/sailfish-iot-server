package com.sailfish.server.core.listener;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.sailfish.server.core.enums.PacketType;
import com.sailfish.server.core.handler.ChannelInitializerFactory;
import com.sailfish.server.core.protocol.MessageProcessor;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.ThreadFactory;

/**
 * 服务器类
 *
 * @author wangpeixin
 * @since 2025/7/9 14:07
 */
@Slf4j
public class ServerListener {

    // 服务器通道
    private Channel serverChannel;

    // 处理新连接
    private EventLoopGroup bossGroup;

    // IO处理
    private EventLoopGroup workerGroup;

    // 协议名称
    private final String protocolName;

    // 消息处理器
    private final MessageProcessor processor;

    private final String serverAddress = "0.0.0.0";

    private final int serverPort = 8080;

    public ServerListener(String protocolName, MessageProcessor processor) {
        this.protocolName = protocolName;
        this.processor = processor;
    }

    /**
     * 开始启动服务
     *
     * @author wangpeixin
     * @since 2025/7/9 14:10
     */
    public void startListener() throws InterruptedException {
        ThreadFactory bossThreadFactory = new ThreadFactoryBuilder()
                .setNameFormat("boss-%d")
                .setDaemon(true)
                .setPriority(Thread.NORM_PRIORITY)
                .build();

        ThreadFactory workerThreadFactory = new ThreadFactoryBuilder()
                .setNameFormat("worker-%d")
                .setDaemon(true)
                .setPriority(Thread.NORM_PRIORITY)
                .build();

        bossGroup = new NioEventLoopGroup(4, bossThreadFactory);
        workerGroup = new NioEventLoopGroup(16, workerThreadFactory);

        // 创建通道初始化器
        ChannelInitializer<SocketChannel> initializer = ChannelInitializerFactory.createInitializer(protocolName, PacketType.BINARY, processor);

        // 创建服务器服务器
        ServerBootstrap server = new ServerBootstrap()
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)

                // 设置TCP连接的队列长度
                .option(ChannelOption.SO_BACKLOG, 128)

                // 允许端口重用
                .option(ChannelOption.SO_REUSEADDR, true)

                // 设置接收缓冲区大小
                .option(ChannelOption.SO_RCVBUF, 65536)

                // 保持连接活跃
                .childOption(ChannelOption.SO_KEEPALIVE, true)

                // 禁用Nagle算法，减少延迟
                .childOption(ChannelOption.TCP_NODELAY, true)

                // 设置发送缓冲区大小
                .childOption(ChannelOption.SO_SNDBUF, 65536)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(initializer);
        // netty初始化 阻塞初始化
        serverChannel = server.bind(serverAddress, serverPort).sync().channel();
        // 把netty服务端线程 设置为wait状态
        // 会阻塞springboot主线程
        serverChannel.closeFuture().sync();
        log.info("Tcp server [{}] started, BindAddress:[{}], BindPort: [{}]", protocolName, serverAddress, serverPort);
    }

    /**
     * 开始停止服务
     *
     * @author wangpeixin
     * @since 2025/7/9 14:11
     */
    @PreDestroy
    public void stopListener() throws InterruptedException {
        if (this.serverChannel != null) {
            ChannelFuture cf = this.serverChannel.close().sync();
            cf.awaitUninterruptibly();
        }

        Future<?> bossFuture = null;
        Future<?> workerFuture = null;

        if (bossGroup != null) {
            bossFuture = bossGroup.shutdownGracefully();
        }
        if (workerGroup != null) {
            workerFuture = workerGroup.shutdownGracefully();
        }

        log.info("[{}] Awaiting shutdown gracefully boss and worker groups...", protocolName);

        if (bossFuture != null) {
            bossFuture.sync();
        }
        if (workerFuture != null) {
            workerFuture.sync();
        }

        log.info("[{}] Protocol server stopped!", protocolName);
    }

    /**
     * 关闭服务器监听器
     *
     * @author wangpeixin
     * @since 2025/7/9 15:21
     */
    public void destroy() throws InterruptedException {
        stopListener();
    }

}
