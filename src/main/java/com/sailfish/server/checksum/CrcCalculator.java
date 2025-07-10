package com.sailfish.server.checksum;

import org.apache.commons.lang3.tuple.Pair;

/**
 * CRC校验接口
 */
public interface CrcCalculator {
    /**
     * 计算CRC校验值
     * @param data 待校验数据
     * @return CRC校验值
     */
    int calculate(byte[] data);

    /**
     * 验证CRC校验值
     * @param data 待校验数据
     * @param checkSum 待验证的校验值
     * @return Pair<是否校验通过, 期望的校验值>
     */
    Pair<Boolean, Integer> validate(byte[] data, int checkSum);

}
