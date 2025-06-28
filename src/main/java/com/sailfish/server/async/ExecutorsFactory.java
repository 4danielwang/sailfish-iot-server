package com.sailfish.server.async;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ExecutorService工厂类
 *
 * @author wangpeixin
 * @since 2025/6/26 17:00
 */
public class ExecutorsFactory {

    // 创建虚拟线程执行器
    public static ExecutorService newVirtualThreadPool(String namePrefix) {
        return Executors.newThreadPerTaskExecutor(new CustomVirtualThreadFactory(namePrefix));
    }

}
