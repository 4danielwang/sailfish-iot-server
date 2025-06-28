/**
 * 开源代码，仅供学习和交流研究使用，商用请联系三丙
 * 微信：mohan_88888
 * 抖音：程序员三丙
 * 付费课程知识星球：https://t.zsxq.com/aKtXo
 */
package com.sailfish.server.codec;

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * 字节工具类
 *
 * @author wangpeixin
 * @since 2025/6/24 16:35
 */
public class ByteUtil {

    public static byte[] uuidToBytes(UUID uuid) {
        ByteBuffer buf = ByteBuffer.allocate(16);
        buf.putLong(uuid.getMostSignificantBits());
        buf.putLong(uuid.getLeastSignificantBits());
        return buf.array();
    }

    public static UUID bytesToUuid(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        long firstLong = bb.getLong();
        long secondLong = bb.getLong();
        return new UUID(firstLong, secondLong);
    }

    public static byte[] stringToBytes(String string) {
        return string.getBytes(StandardCharsets.UTF_8);
    }

    public static String bytesToString(byte[] data) {
        return new String(data, StandardCharsets.UTF_8);
    }

    public static byte[] longToBytes(long x) {
        ByteBuffer longBuffer = ByteBuffer.allocate(Long.BYTES);
        longBuffer.putLong(0, x);
        return longBuffer.array();
    }

    public static long bytesToLong(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getLong();
    }

    /**
     * ByteBuf转byte数组
     *
     * @param byteBuf
     * @return
     */
    public static byte[] toBytes(ByteBuf byteBuf) {
        int msgLength = byteBuf.readableBytes();
        byte[] bytes = new byte[msgLength];
        byteBuf.readBytes(bytes);
        return bytes;
    }
}
