package com.sailfish.server.core.command;

/**
 * 下行命令执行器
 */
public interface DownlinkCommandExecutor<T> extends CommandExecutor {

    // 执行命令
    void execute();
}
