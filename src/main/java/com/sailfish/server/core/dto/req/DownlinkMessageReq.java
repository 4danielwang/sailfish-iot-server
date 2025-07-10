package com.sailfish.server.core.dto.req;

/**
 * 业务系统（设备中心）下行消息对象
 *
 * @param id 消息id
 * @param sessionId 会话id
 * @param protocolName 协议名称
 * @param pileNo 充电桩编号
 * @param cmd 下行命令编号
 * @param requestId 请求id
 * @author wangpeixin
 * @since 2025/7/8 15:50
 */
public record DownlinkMessageReq(String id,
                                 String sessionId,
                                 String protocolName,
                                 String pileNo,
                                 String cmd,
                                 String requestId){}
