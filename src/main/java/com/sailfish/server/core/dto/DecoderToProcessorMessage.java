package com.sailfish.server.core.dto;

import com.sailfish.server.core.session.ProtocolSession;

/**
 * 入站处理器 -> 消息处理器
 *
 * @param id 消息唯一标识符
 * @param content 消息体
 * @param session 会话
 * @author wangpeixin
 * @since 2025/7/8 15:24
 */
public record DecoderToProcessorMessage(String id,
                                        byte[] content,
                                        ProtocolSession session){}
