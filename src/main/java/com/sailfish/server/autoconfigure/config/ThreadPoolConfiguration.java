package com.sailfish.server.autoconfigure.config;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 线程池配置
 *
 * @author wangpeixin
 * @since 2025/7/9 11:23
 */
public class ThreadPoolConfiguration {

    // 用来处理caffine异步操作的线程池
    public static final ExecutorService COMMON_IO_THREAD_POOL = Executors.newVirtualThreadPerTaskExecutor();
}
