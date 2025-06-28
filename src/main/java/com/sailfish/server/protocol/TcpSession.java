package com.sailfish.server.protocol;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.sailfish.server.command.domain.YkcDownlinkMsg;
import com.sailfish.server.protocol.enums.SeqNumLenEnum;
import com.sailfish.server.protocol.enums.SessionCloseReason;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.io.IOException;
import java.net.SocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * TCP会话
 *
 * @author wangpeixin
 * @since 2025/6/25 10:03
 */
@Slf4j
@Data
public class TcpSession implements Closeable {

    // 当前会话的序列号
    private final AtomicInteger sequenceNumber = new AtomicInteger(0);

    // 请求缓存容量
    private static final int REQUEST_CACHE_LIMIT = 1000;

    // 记录客户端地址
    private SocketAddress clientAddr;

    private ChannelHandlerContext ctx;

    private final Cache<String,Object> requestCache = Caffeine.newBuilder()
            .initialCapacity(REQUEST_CACHE_LIMIT)
            .maximumSize(REQUEST_CACHE_LIMIT)
            .expireAfterAccess(Duration.ofMinutes(1))
            .build();

    private final UUID id;

    private LocalDateTime lastActivityTime;

    // TODO：支持边缘网关 支持多台设备
    private final Set<String> pileCodeSet;

    // 依赖翻转 注入外部writeAndFlush逻辑
    private final Consumer<ByteBuf> writeAndFlushConsumer;

    // 子类必须实现
    public TcpSession(Consumer<ByteBuf> writeAndFlushConsumer) {
        this.id = UUID.randomUUID();
        this.lastActivityTime = LocalDateTime.now();
        this.pileCodeSet = new LinkedHashSet<>();
        this.writeAndFlushConsumer = writeAndFlushConsumer;
    }


    // 销毁session
    public void doClose(SessionCloseReason reason) {
        log.info("[{}] 会话关闭，原因: {}", this, reason);
        ctx.flush();
        ctx.close();
    }

    // 增加电桩
    public void addPileCode(String pileCode) {
        this.pileCodeSet.add(pileCode);
    }

    /**
     * 获取下一个序列号
     */
    public int nextSeqNo(SeqNumLenEnum length) {
        synchronized (sequenceNumber) {
            int result = sequenceNumber.incrementAndGet();
            // 如果序列号溢出，则重置为0
            switch (length) {
                case BYTE -> {
                    if (result == 0xFF) {
                        sequenceNumber.set(0);
                    }
                }
                case SHORT -> {
                    if (result == Short.MAX_VALUE) {
                        sequenceNumber.set(0);
                    }
                }
                default -> {
                    if (result == Integer.MAX_VALUE) {
                        sequenceNumber.set(0);
                    }
                }
            }

            return result;
        }
    }

    public void writeAndFlush(ByteBuf byteBuf) {
        writeAndFlushConsumer.accept(byteBuf);
    }

    @Override
    public void close() throws IOException {
        doClose(SessionCloseReason.DESTRUCTION);
    }
}
