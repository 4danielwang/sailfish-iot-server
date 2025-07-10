# Sailfish Netty Server

一个基于 Netty 框架的 Java 服务器项目。

## 项目结构

```
sailfish-netty-server/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/sailfish/
│   │   │       ├── server/
│   │   │       │   └── NettyServer.java
│   │   │       ├── handler/
│   │   │       │   └── ServerHandler.java
│   │   │       └── config/
│   │   │           └── ServerConfig.java
│   │   └── resources/
│   │       └── logback.xml
│   └── test/
│       └── java/
│           └── com/sailfish/
│               └── NettyServerTest.java
├── pom.xml
└── README.md
```

## 功能特性

- 基于 Netty 4.1.94.Final
- 支持字符串消息的编解码
- 内置日志系统（SLF4J + Logback）
- 可配置的服务器参数
- 简单的消息处理逻辑

## 环境要求

- Java 11 或更高版本
- Maven 3.6 或更高版本

## 快速开始

### 1. 编译项目

```bash
mvn clean compile
```

### 2. 运行测试

```bash
mvn test
```

### 3. 启动服务器

```bash
# 使用默认端口 8080
mvn exec:java -Dexec.mainClass="com.sailfish.server.NettyServer"

# 或指定端口
mvn exec:java -Dexec.mainClass="com.sailfish.server.NettyServer" -Dexec.args="9090"
```

### 4. 打包项目

```bash
mvn clean package
```

打包后会生成可执行的 JAR 文件：`target/sailfish-netty-server-1.0.0.jar`

### 5. 运行 JAR 文件

```bash
# 使用默认端口
java -jar target/sailfish-netty-server-1.0.0.jar

# 指定端口
java -jar target/sailfish-netty-server-1.0.0.jar 9090
```

## 测试连接

使用 telnet 或 nc 工具测试服务器：

```bash
telnet localhost 8080
```

支持的命令：

- `hello` - 获取问候消息
- `time` - 获取当前时间
- `quit` - 退出连接
- 其他任意消息 - 回显收到的消息

## 配置说明

服务器配置参数可以通过修改 `ServerConfig` 类来调整：

- `port` - 监听端口（默认：8080）
- `bossThreads` - Boss 线程数（默认：1）
- `workerThreads` - Worker 线程数（默认：CPU 核心数 \* 2）
- `backlog` - 连接队列大小（默认：128）
- `keepAlive` - 是否启用 TCP Keep-Alive（默认：true）

## 日志配置

日志配置在 `src/main/resources/logback.xml` 中：

- 控制台输出：INFO 级别及以上
- 文件输出：保存到 `logs/sailfish-server.log`
- 日志轮转：按天轮转，保留 30 天

## 开发说明

### 添加新的处理器

1. 在 `src/main/java/com/sailfish/handler/` 目录下创建新的 Handler 类
2. 继承 `ChannelInboundHandlerAdapter` 或 `SimpleChannelInboundHandler`
3. 在 `NettyServer` 的 pipeline 中添加新的 Handler

### 添加新的依赖

在 `pom.xml` 的 `<dependencies>` 部分添加新的依赖项。

## 许可证

MIT License
