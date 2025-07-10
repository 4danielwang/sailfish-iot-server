package com.sailfish.server.core.command;

import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 命令关系管理器（单例）
 * 维护命令之间 请求-应答 的关系
 *
 * @author wangpeixin
 */
public class CommandManager {

    private static final CommandManager INSTANCE = new CommandManager();

    private final Map<Command, Command> requestToResponseMap = new ConcurrentHashMap<>();
    private final Map<Command, Command> responseToRequestMap = new ConcurrentHashMap<>();

    private CommandManager() {
        // Private constructor to enforce singleton
    }

    static {
        // 使用Java SPI机制自动加载并执行所有命令注册服务
        ServiceLoader<CommandRegistration> loader = ServiceLoader.load(CommandRegistration.class);
        for (CommandRegistration registration : loader) {
            registration.register(INSTANCE);
        }
    }

    public static CommandManager getInstance() {
        return INSTANCE;
    }

    /**
     * 注册一对请求和应答命令
     * @param request  请求命令
     * @param response 应答命令
     */
    public void register(Command request, Command response) {
        requestToResponseMap.put(request, response);
        responseToRequestMap.put(response, request);
    }

    /**
     * 根据请求命令获取对应的应答命令
     * @param request 请求命令
     * @return Optional包装的应答命令
     */
    public Optional<Command> getResponse(Command request) {
        return Optional.ofNullable(requestToResponseMap.get(request));
    }

    /**
     * 根据应答命令获取对应的请求命令
     * @param response 应答命令
     * @return Optional包装的请求命令
     */
    public Optional<Command> getRequest(Command response) {
        return Optional.ofNullable(responseToRequestMap.get(response));
    }

    /**
     * 判断一个命令是否是请求命令
     * @param command 待检查的命令
     * @return 如果是请求命令则为true
     */
    public boolean isRequest(Command command) {
        return requestToResponseMap.containsKey(command);
    }

    /**
     * 判断一个命令是否是应答命令
     * @param command 待检查的命令
     * @return 如果是应答命令则为true
     */
    public boolean isResponse(Command command) {
        return responseToRequestMap.containsKey(command);
    }
}
