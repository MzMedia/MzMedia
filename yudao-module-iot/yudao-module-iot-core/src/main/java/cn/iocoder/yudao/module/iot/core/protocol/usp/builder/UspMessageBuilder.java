package cn.iocoder.yudao.module.iot.core.protocol.usp.builder;

import cn.hutool.core.util.IdUtil;
import cn.iocoder.yudao.module.iot.core.protocol.usp.codec.UspProtobufCodec;
import cn.iocoder.yudao.module.iot.core.protocol.usp.model.*;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * TR-369 USP 消息便捷构建器
 *
 * @author 芋道源码
 */
public class UspMessageBuilder {

    public static final String DEFAULT_CONTROLLER_ID = "proto::mzmedia-controller";

    /**
     * 构建 USP GET 消息
     */
    public static UspMsg buildGetMsg(List<String> paths) {
        return UspMsg.builder()
                .msgId(IdUtil.fastSimpleUUID())
                .msgType(UspMsgType.GET)
                .getRequest(UspGetRequest.builder()
                        .paramPaths(paths)
                        .maxDepth(1)
                        .build())
                .build();
    }

    /**
     * 构建 USP GET 消息（单个路径）
     */
    public static UspMsg buildGetMsg(String path) {
        return buildGetMsg(Collections.singletonList(path));
    }

    /**
     * 构建 USP SET 消息
     */
    public static UspMsg buildSetMsg(Map<String, String> params) {
        return UspMsg.builder()
                .msgId(IdUtil.fastSimpleUUID())
                .msgType(UspMsgType.SET)
                .setRequest(UspSetRequest.builder()
                        .params(params)
                        .allowPartial(true)
                        .build())
                .build();
    }

    /**
     * 构建 USP OPERATE 消息
     */
    public static UspMsg buildOperateMsg(String command, String commandKey, Map<String, String> args) {
        return UspMsg.builder()
                .msgId(IdUtil.fastSimpleUUID())
                .msgType(UspMsgType.OPERATE)
                .operateRequest(UspOperateRequest.builder()
                        .command(command)
                        .commandKey(commandKey)
                        .sendResp(true)
                        .inputArgs(args)
                        .build())
                .build();
    }

    /**
     * 构建打包好的 USP Record
     */
    public static UspRecord buildRecord(UspMsg msg, String toId, String fromId) throws IOException {
        byte[] payload = UspProtobufCodec.encodeMsg(msg);
        return UspRecord.builder()
                .version("1.3")
                .toId(toId)
                .fromId(fromId != null ? fromId : DEFAULT_CONTROLLER_ID)
                .payloadSecurity(0)
                .payload(payload)
                .build();
    }

    /**
     * 直接构建并序列化为传输层二进制字节
     */
    public static byte[] buildRecordBytes(UspMsg msg, String toId, String fromId) throws IOException {
        UspRecord record = buildRecord(msg, toId, fromId);
        return UspProtobufCodec.encodeRecord(record);
    }

}
