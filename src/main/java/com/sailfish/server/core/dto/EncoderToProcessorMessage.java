package com.sailfish.server.core.dto;

import com.sailfish.server.core.dto.req.DownlinkMessageReq;
import com.sailfish.server.core.session.ProtocolSession;


/**
 * 消息处理器 -> Encoder
 *
 * @param id 消息唯一标识符
 * @param msg 从业务系统接收到消息
 * @param session 会话
 * @author wangpeixin
 * @since 2025/7/8 16:41
 */
public record EncoderToProcessorMessage(String id,
                                        DownlinkMessageReq msg,
                                        ProtocolSession session
                                        ){}
