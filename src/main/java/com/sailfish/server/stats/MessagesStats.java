/**
 * 开源代码，仅供学习和交流研究使用，商用请联系三丙
 * 微信：mohan_88888
 * 抖音：程序员三丙
 * 付费课程知识星球：https://t.zsxq.com/aKtXo
 */
package com.sailfish.server.stats;

/**
 * 消息状态
 *
 * @author wangpeixin
 * @since 2025/6/23 16:17
 */
public interface MessagesStats {
    default void incrementTotal() {
        incrementTotal(1);
    }

    void incrementTotal(int amount);


    default void incrementSuccessful() {
        incrementSuccessful(1);
    }

    // 消息成功
    void incrementSuccessful(int amount);

    default void incrementFailed() {
        incrementFailed(1);
    }

    // 消息是被
    void incrementFailed(int amount);

    // 总消息数
    int getTotal();

    // 成功消息数
    int getSuccessful();

    // 失败消息数
    int getFailed();

    // 重置
    void reset();
}
