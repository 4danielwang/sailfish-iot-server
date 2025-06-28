/**
 * 开源代码，仅供学习和交流研究使用，商用请联系三丙
 * 微信：mohan_88888
 * 抖音：程序员三丙
 * 付费课程知识星球：https://t.zsxq.com/aKtXo
 */
package com.sailfish.server.stats;

import io.micrometer.core.instrument.Counter;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 计数器抽象类
 *
 * @author wangpeixin
 * @since 2025/6/23 16:26
 */
public abstract class AbstractCounter {
    // 本地计数器
    private final AtomicInteger localCounter;

    // 指标计数器 用于上报监控系统
    private final Counter metricsCounter;

    public AbstractCounter(AtomicInteger localCounter, Counter metricsCounter) {
        this.localCounter = localCounter;
        this.metricsCounter = metricsCounter;
    }

    public void increment() {
        localCounter.incrementAndGet();
        metricsCounter.increment();
    }

    public void clear() {
        localCounter.set(0);
    }

    public int get() {
        return localCounter.get();
    }

    public void add(int delta){
        localCounter.addAndGet(delta);
        metricsCounter.increment(delta);
    }
}
