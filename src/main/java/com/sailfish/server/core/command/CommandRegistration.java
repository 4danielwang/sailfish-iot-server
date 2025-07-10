package com.sailfish.server.core.command;

/**
 * 命令注册服务接口 (SPI)
 * 允许CommandManager自动发现并注册命令关系
 */
public interface CommandRegistration {

    /**
     * 在此方法中注册该服务负责的命令关系
     * @param manager 命令管理器实例
     */
    void register(CommandManager manager);

}
