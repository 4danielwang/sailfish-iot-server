package com.sailfish.server.listener;

import com.sailfish.server.ProtocolContext;
import com.sailfish.server.forwarder.RocketMQForwarder;
import com.sailfish.server.handler.*;
import com.sailfish.server.protocol.provider.DefaultSessionProvider;
import com.sailfish.server.protocol.provider.SessionProvider;
import com.sailfish.server.protocol.ykc150.YkcProtocolMsgProcessor;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * netty服务器
 *
 * @author wangpeixin
 * @since 2025/6/23 08:45
 */
public class NettyServer {
    public static void main(String[] args) throws InterruptedException {
        ServerBootstrap server = new ServerBootstrap();
        EventLoopGroup parentGroup = new NioEventLoopGroup();
        EventLoopGroup childGroup =new NioEventLoopGroup();
        server.group(parentGroup, childGroup);

        server.option(ChannelOption.SO_BACKLOG, 1024);

        server.channel(NioServerSocketChannel.class);

        // 消息转发器
        RocketMQForwarder forwarder = new RocketMQForwarder();

        // 全局上下文
        SessionProvider sessionProvider = new DefaultSessionProvider();
        ProtocolContext ctx = new ProtocolContext(sessionProvider);
        YkcProtocolMsgProcessor processor = new YkcProtocolMsgProcessor(forwarder, ctx);

        // 只处理入站请求，出站直接由具体的CmdExecutor处理
        server.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch){
                ch.pipeline().addLast("IdleStateHandler", new IdleStateHandler(
                        60, 0, 0));
                ch.pipeline().addLast("IdleEventHandler", new IdleEventHandler());
                // 帧解析入站处理器
                ch.pipeline().addLast("frameDecoder", new CustomLengthFieldFrameDecoder());
                ch.pipeline().addLast("tcpMsgDecoder", new TcpMsgDecoder());
                // 处理上行协议消息
                ch.pipeline().addLast("uplinkHandler", new UplinkHandler(processor));
            }
        });

        //第4步绑定8080端口
        ChannelFuture future = server.bind(8080).sync();
        System.out.println("Server started on port " + "8080");
        //当通道关闭了，就继续往下走
        future.channel().closeFuture().sync();
    }
}
