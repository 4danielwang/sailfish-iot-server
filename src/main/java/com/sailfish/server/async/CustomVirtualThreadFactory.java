package com.sailfish.server.async;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 自定义虚拟线程池工厂
 *
 * @author wangpeixin
 * @since 2025/6/26 16:57
 */
public class CustomVirtualThreadFactory implements ThreadFactory {
    private final String prefix;
    private final AtomicLong threadNumber = new AtomicLong(1);

    public CustomVirtualThreadFactory(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public Thread newThread(Runnable r) {
        return Thread.ofVirtual().name(prefix + "-" + threadNumber.getAndIncrement()).unstarted(r);
    }
}
