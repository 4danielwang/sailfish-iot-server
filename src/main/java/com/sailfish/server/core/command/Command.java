package com.sailfish.server.core.command;

/**
 * 命令的顶层接口
 */
public interface Command {

    /**
     * 获取命令的唯一标识/编号
     * @return 字符串形式的命令编号 (e.g., "0x01")
     */
    String getCode();

    /**
     * 获取命令的描述信息
     * @return 描述
     */
    String getDescription();

    /**
     * 判断该命令是否需要一个ACK（应答）
     * 默认实现是通过查询CommandManager中是否注册了应答命令来判断
     * @return 如果需要应答则返回true
     */
    default boolean isAckRequired() {
        return CommandManager.getInstance().getResponse(this).isPresent();
    }
}
