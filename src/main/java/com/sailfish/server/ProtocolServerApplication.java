package com.sailfish.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author wangpeixin
 * @since 2025/7/14 10:41
 */
@SpringBootApplication
@EnableAsync
public class ProtocolServerApplication{

    public static void main(String[] args) {
        SpringApplication.run(ProtocolServerApplication.class, args);
        System.out.println("验证springboot主线程是否被阻塞");
    }
}
