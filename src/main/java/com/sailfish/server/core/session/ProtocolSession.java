package com.sailfish.server.core.session;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.sailfish.server.core.broker.Forwarder;
import com.sailfish.server.core.enums.SessionCloseReason;
import com.sailfish.server.common.util.IDUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.net.SocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 代表一个客户端连接会话
 */
@Slf4j
@Data
public class ProtocolSession implements Closeable {

    private static final int REQUEST_CACHE_LIMIT = 1000;

    public static final AttributeKey<String> SESSION_KEY = AttributeKey.valueOf("session_id");

    private final String id;

    private final String protocolName;

    private final Set<String> pileNos;

    private final Cache<String, Object> requestCache = Caffeine.newBuilder()
            .initialCapacity(REQUEST_CACHE_LIMIT)
            .maximumSize(REQUEST_CACHE_LIMIT)
            .expireAfterAccess(Duration.ofMinutes(1))
            .build();

    private final AtomicInteger seqNo = new AtomicInteger(0);

    private LocalDateTime lastActiveTime;

    private Forwarder forwarder;

    private SocketAddress ip;

    private ChannelHandlerContext ctx;


    public ProtocolSession(String protocolName) {
        this.id = IDUtil.generateID();
        this.protocolName = protocolName;
        this.lastActiveTime = LocalDateTime.now();
        this.pileNos = new LinkedHashSet<>();
    }

    /**
     * 绑定充电桩编号
     * @param pileNo 充电桩编号
     */
    public void bindPileNo(String pileNo) {
        pileNos.add(pileNo);
    }

    /**
     * 解绑充电桩编号
     * @param pileNo 充电桩编号
     */
    public void unbindPileNo(String pileNo) {
        pileNos.remove(pileNo);
    }

    /**
     * 刷新最近活跃时间
     */
    public void refreshLastActiveTime() {
        this.lastActiveTime = LocalDateTime.now();
    }

    /**
     * 获取下一个序列号
     */
    public int nextSeqNo(SeqNoLength length) {
        synchronized (seqNo) {
            int result = seqNo.incrementAndGet();
            // 如果序列号溢出，则重置为0
            switch (length) {
                case BYTE -> {
                    if (result == 0xFF) {
                        seqNo.set(0);
                    }
                }
                case SHORT -> {
                    if (result == Short.MAX_VALUE) {
                        seqNo.set(0);
                    }
                }
                default -> {
                    if (result == Integer.MAX_VALUE) {
                        seqNo.set(0);
                    }
                }
            }

            return result;
        }
    }


    @Override
    public void close(){
        close(SessionCloseReason.DESTRUCTION);
    }

    private void close(SessionCloseReason reason) {
        log.info("[{}] Protocol会话关闭，原因: {}", this, reason);

        ctx.flush();
        ctx.close();
    }

    /**
     * 序列号长度
     * @return 会话ID
     */
    public enum SeqNoLength {

        // 1字节
        BYTE,

        // 2字节
        SHORT,

        // 4字节
        INT,

    }

    public void writeAndFlush(ByteBuf byteBuf){
        if (ctx.isRemoved()) {
            close(SessionCloseReason.INACTIVE);
            log.warn("[{}]{}{} TCP会话已失效，因此删除会话", protocolName, ctx.channel(), this);
            return;
        }

        try {
            if (Objects.isNull(byteBuf)) {
                log.warn("[{}]{}{} 下发空报文被拦截", protocolName, ctx.channel(), this);
            }

            if (log.isDebugEnabled()) {
                log.debug("[{}]{} 开始发送下行报文:{}", protocolName, this, ByteBufUtil.hexDump(byteBuf));
            }

            ctx.writeAndFlush(Unpooled.wrappedBuffer(byteBuf))
                    .addListener(this::logListener);

        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 记录下行报文发送结果
     *
     * @author wangpeixin
     * @since 2025/7/8 15:10
     */
    private void logListener(Future<? super Void> channelFuture) {
        if (channelFuture.isDone() && !channelFuture.isSuccess()) {
            log.info("[{}]{} 下行报文发送未成功", protocolName, this);
        }else{
            log.info("[{}]{} 下行报文发送成功", protocolName, this);
        }
    }

}
