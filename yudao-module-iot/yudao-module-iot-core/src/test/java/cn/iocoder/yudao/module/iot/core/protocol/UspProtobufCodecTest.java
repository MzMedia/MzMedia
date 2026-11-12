package cn.iocoder.yudao.module.iot.core.protocol;

import cn.iocoder.yudao.module.iot.core.protocol.usp.builder.UspMessageBuilder;
import cn.iocoder.yudao.module.iot.core.protocol.usp.codec.UspProtobufCodec;
import cn.iocoder.yudao.module.iot.core.protocol.usp.model.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TR-369 USP 二进制 Protobuf 编解码测试
 */
public class UspProtobufCodecTest {

    @Test
    public void testRecordEncodeDecode() throws IOException {
        byte[] testPayload = "hello-usp-payload".getBytes();
        UspRecord originalRecord = UspRecord.builder()
                .version("1.3")
                .toId("proto::agent-001")
                .fromId("proto::mzmedia-controller")
                .payloadSecurity(0)
                .payload(testPayload)
                .build();

        byte[] encoded = UspProtobufCodec.encodeRecord(originalRecord);
        assertNotNull(encoded);
        assertTrue(encoded.length > 0);

        UspRecord decoded = UspProtobufCodec.decodeRecord(encoded);
        assertNotNull(decoded);
        assertEquals("1.3", decoded.getVersion());
        assertEquals("proto::agent-001", decoded.getToId());
        assertEquals("proto::mzmedia-controller", decoded.getFromId());
        assertEquals(0, decoded.getPayloadSecurity());
        assertArrayEquals(testPayload, decoded.getPayload());
    }

    @Test
    public void testMsgEncodeDecode() throws IOException {
        // 1. 测试 Get 消息
        UspMsg getMsg = UspMessageBuilder.buildGetMsg("Device.DeviceInfo.Manufacturer");
        byte[] encodedGet = UspProtobufCodec.encodeMsg(getMsg);
        assertNotNull(encodedGet);

        UspMsg decodedGet = UspProtobufCodec.decodeMsg(encodedGet);
        assertNotNull(decodedGet);
        assertEquals(getMsg.getMsgId(), decodedGet.getMsgId());
        assertEquals(UspMsgType.GET, decodedGet.getMsgType());

        // 2. 测试 Operate 消息
        Map<String, String> args = new HashMap<>();
        args.put("Host", "8.8.8.8");
        UspMsg opMsg = UspMessageBuilder.buildOperateMsg("Device.IP.Diagnostics.IPPing()", "KEY_123", args);
        byte[] encodedOp = UspProtobufCodec.encodeMsg(opMsg);
        assertNotNull(encodedOp);

        UspMsg decodedOp = UspProtobufCodec.decodeMsg(encodedOp);
        assertNotNull(decodedOp);
        assertEquals(opMsg.getMsgId(), decodedOp.getMsgId());
        assertEquals(UspMsgType.OPERATE, decodedOp.getMsgType());

        // 3. 测试 Record 打包完整二进制
        byte[] fullRecordBytes = UspMessageBuilder.buildRecordBytes(opMsg, "proto::agent-001", "proto::mzmedia-controller");
        assertNotNull(fullRecordBytes);
        UspRecord record = UspProtobufCodec.decodeRecord(fullRecordBytes);
        assertNotNull(record);
        assertEquals("proto::agent-001", record.getToId());
        UspMsg unmarshaledMsg = UspProtobufCodec.decodeMsg(record.getPayload());
        assertNotNull(unmarshaledMsg);
        assertEquals(UspMsgType.OPERATE, unmarshaledMsg.getMsgType());
    }

}
