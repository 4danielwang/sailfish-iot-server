/**
 * 开源代码，仅供学习和交流研究使用，商用请联系三丙
 * 微信：mohan_88888
 * 抖音：程序员三丙
 * 付费课程知识星球：https://t.zsxq.com/aKtXo
 */
package com.sailfish.server.command.domain;

import com.sailfish.server.protocol.enums.YkcDownlinkCmdEnum;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Optional;
import java.util.UUID;

/**
 * 下行消息
 *
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class YkcDownlinkMsg implements Serializable {

    // 消息ID
    private UUID id;

    // 请求消息ID optional
    private UUID requestId;

    // 指令
    private int cmd;

    // TODO: 下行消息体

    // TODO: 上行请求消息（optional）
}
