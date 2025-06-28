package com.sailfish.server;

import cn.hutool.core.util.HexUtil;
import com.sailfish.server.codec.ByteUtil;
import com.sailfish.server.utils.CrcUtil;
import io.netty.buffer.ByteBufUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @Author: wangpeixin
 **/
@SpringBootTest
class ProtocolContextTest {

    @Test
    public void testCrc(){
        byte[] calc3 = new byte[] {0x68, (byte) 0x90};
        // 长度13
        // 68 0D 00 00 00 03 32 36 31 36 38 33 39 01 00 67 F2
        // 00 00 00 03 32 36 31 36 38 33 39 01 00
        byte[] cmd3 = new byte[] {0x00, 0x00, 0x00,0x03,0x32,0x36,0x31,0x36,0x38,0x33,0x39,0x01,0x00};

        int crc1 = CrcUtil.crcSum(cmd3);
    }
}
