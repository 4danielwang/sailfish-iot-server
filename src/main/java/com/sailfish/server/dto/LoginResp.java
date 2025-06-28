package com.sailfish.server.dto;

/**
 * 登录响应dto
 * 只包含消息体内容
 *
 * @author wangpeixin
 * @since 2025/6/26 14:34
 */
public record LoginResp(Boolean success,
                        String pileCode) {
}
