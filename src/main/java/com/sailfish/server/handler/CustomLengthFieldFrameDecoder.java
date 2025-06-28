package com.sailfish.server.handler;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * 自定义LengthFieldDecoder
 * 解析帧格式
 * 只负责长度字段处理
 * 只处理粘包拆包
 *
 * @author wangpeixin
 * @since 2025/6/23 10:33
 */
public class CustomLengthFieldFrameDecoder extends LengthFieldBasedFrameDecoder {

    // 最大数据长度
    public static final int MAX_DATA_LENGTH = 200;

    // 取2的幂次方 256
    // 1 + 1 + 2 + 1 + 1 + 200 + 2 = 208
    public static final int MAX_FRAME_LENGTH = 256;

    // 长度域偏移量
    public static final int LENGTH_FIELD_OFFSET = 1;

    // 长度域长度
    public static final int LENGTH_FIELD_LENGTH = 1;

    // 长度调整
    public static final int LENGTH_ADJUSTMENT = 2;

    // 初始字节剥离
    public static final int INITIAL_BYTES_TO_STRIP = 0;


    public CustomLengthFieldFrameDecoder() {
        super(MAX_FRAME_LENGTH, LENGTH_FIELD_OFFSET, LENGTH_FIELD_LENGTH,
                LENGTH_ADJUSTMENT, INITIAL_BYTES_TO_STRIP);
    }

}
