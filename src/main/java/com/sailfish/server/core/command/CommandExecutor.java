package com.sailfish.server.core.command;

/**
 * 命令执行器接口
 *
 * @author wangpeixin
 */
public interface CommandExecutor{

    /**
     * 获取此执行器负责的命令
     *
     * @return 命令
     */
    Command getCommand();
}
