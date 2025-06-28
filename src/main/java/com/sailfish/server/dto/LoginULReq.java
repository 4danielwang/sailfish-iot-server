package com.sailfish.server.dto;

import lombok.Builder;

/**
 * 登录请求dto
 * TODO：用protobuf重构一下
 * @author wangpeixin
 * @since 2025/6/26 14:13
 */
@Builder
public record LoginULReq(String pileCode,
                         String credential,
                         String remoteAddr,
                         String additionalInfo) {

    @Override
    public String toString() {
        return "LoginRequest{" +
                "pileCode='" + pileCode + '\'' +
                ", credential='" + credential + '\'' +
                ", remoteAddr='" + remoteAddr + '\'' +
                ", additionalInfo='" + additionalInfo + '\'' +
                '}';
    }
}
