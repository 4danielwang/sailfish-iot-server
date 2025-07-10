package com.sailfish.server.core.command;

/**
 * 上行命令执行器
 */
public interface UplinkCommandExecutor<T> extends CommandExecutor {

    // 执行命令
    void execute() throws Exception;
}
