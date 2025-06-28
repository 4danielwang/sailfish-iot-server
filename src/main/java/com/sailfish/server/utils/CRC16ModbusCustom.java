package com.sailfish.server.utils;

import cn.hutool.core.io.checksum.crc16.CRC16Checksum;

/**
 * CRC-16 (0x180D) 实现
 * 多项式：0x180D，初始值0x0000，低位在前，高位在后
 */
public class CRC16ModbusCustom extends CRC16Checksum {
    private static final long serialVersionUID = 1L;
    private static final int POLY = 0x180D;

    @Override
    public void reset() {
        this.wCRCin = 0x0000; // 初始值
    }

    @Override
    public void update(int b) {
        wCRCin ^= (b & 0xFF);
        for (int j = 0; j < 8; j++) {
            if ((wCRCin & 0x0001) != 0) {
                wCRCin = (wCRCin >> 1) ^ POLY;
            } else {
                wCRCin >>= 1;
            }
        }
    }
}
