package com.sailfish.server.checksum;

import org.apache.commons.lang3.tuple.Pair;

/**
 * CRC校验器抽象类，提供通用实现
 */
public abstract class AbstractCrcCalculator implements CrcCalculator {
    @Override
    public Pair<Boolean, Integer> validate(byte[] data, int checkSum) {
        int expectedCs = calculate(data);
        return Pair.of(expectedCs == checkSum, expectedCs);
    }

    /**
     * 将int转换为byte数组
     * @param value 待转换的int值
     * @return byte数组
     */
    protected abstract byte[] crcToBytesArray(int value);
}
