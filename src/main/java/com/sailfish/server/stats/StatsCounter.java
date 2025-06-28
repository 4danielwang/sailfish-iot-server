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
 * 聚合计数器
 *
 * @author wangpeixin
 * @since 2025/6/23 16:24
 */
public class StatsCounter extends AbstractCounter {
    // 计数器名称
    private final String name;

    public StatsCounter(AtomicInteger localCounter, Counter metricsCounter, String name) {
        super(localCounter, metricsCounter);
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
