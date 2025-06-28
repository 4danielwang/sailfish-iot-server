package com.sailfish.server.listener;

import com.sailfish.server.handler.SimpleClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;

import java.util.Scanner;

/**
 * netty客户端
 *
 * @author wangpeixin
 * @since 2025/6/23 09:04
 */
public class NettyClientDemo {
    public static void main(String[] args) throws InterruptedException {
        // 首先，netty通过ServerBootstrap启动服务端
        Bootstrap client = new Bootstrap();

        //第1步 定义线程组，处理读写和链接事件，没有了accept事件
        EventLoopGroup group = new NioEventLoopGroup();
        client.group(group );

        //第2步 绑定客户端通道
        client.channel(NioSocketChannel.class);

        //第3步 给NIoSocketChannel初始化handler， 处理读写事件
        client.handler(new ChannelInitializer<NioSocketChannel>() {  //通道是NioSocketChannel
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
                //字符串编码器，一定要加在SimpleClientHandler 的上面
//                ch.pipeline().addLast(new StringEncoder());
//                ch.pipeline().addLast(new StringDecoder());
                //添加字符串解码器和编码器
                ch.pipeline().addLast(new ByteArrayEncoder());
                ch.pipeline().addLast(new ByteArrayDecoder());
                ch.pipeline().addLast(new SimpleClientHandler());
            }
        });

        //连接服务器
        ChannelFuture future = client.connect("localhost", 8080).sync();


        //发送数据给服务器
        Scanner scanner = new Scanner(System.in);

        System.out.println("请输入要发送给服务器的消息（输入'exit'退出）:");

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if ("exit".equalsIgnoreCase(line.trim())) {
                break;
            }
            future.channel().writeAndFlush(line);
        }

        //当通道关闭了，就继续往下走
        future.channel().closeFuture().sync();
    }
}
