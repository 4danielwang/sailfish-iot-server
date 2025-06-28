package com.sailfish.server.listener;

import com.sailfish.server.handler.SimpleServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * netty服务器
 *
 * @author wangpeixin
 * @since 2025/6/23 08:45
 */
public class NettyServerDemo {
    public static void main(String[] args) throws InterruptedException {
        ServerBootstrap server = new ServerBootstrap();
        EventLoopGroup parentGroup = new NioEventLoopGroup();
        EventLoopGroup childGroup =new NioEventLoopGroup();
        server.group(parentGroup, childGroup);

        server.option(ChannelOption.SO_BACKLOG, 1024);

        server.channel(NioServerSocketChannel.class);

        server.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch){
                ch.pipeline().addLast(new StringDecoder());
                ch.pipeline().addLast(new StringEncoder());
                ch.pipeline().addLast(new SimpleServerHandler());
            }
        });

        //第4步绑定8080端口
        ChannelFuture future = server.bind(8080).sync();
        System.out.println("Server started on port " + "8080");
        //当通道关闭了，就继续往下走
        future.channel().closeFuture().sync();
    }
}
