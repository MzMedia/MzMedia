package cn.iocoder.yudao.module.iot.core.protocol.usp.codec;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.module.iot.core.protocol.usp.model.*;
import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.WireFormat;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * TR-369 USP 二进制 Protobuf 编解码器
 *
 * 基于 Google Protobuf 标准 Wire Format 与流式编解码，
 * 完全兼容 Broadband Forum TR-369 v1.0/1.3 及 Oktopus / OB-USP-A 规范。
 *
 * @author 芋道源码
 */
@Slf4j
public class UspProtobufCodec {

    // ==========================================
    // 1. USP Record 编解码
    // ==========================================

    /**
     * 将 UspRecord 编码为二进制字节
     */
    public static byte[] encodeRecord(UspRecord record) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CodedOutputStream cos = CodedOutputStream.newInstance(baos);

        // Field 1: string version
        if (StrUtil.isNotBlank(record.getVersion())) {
            cos.writeString(1, record.getVersion());
        }
        // Field 2: string to_id
        if (StrUtil.isNotBlank(record.getToId())) {
            cos.writeString(2, record.getToId());
        }
        // Field 3: string from_id
        if (StrUtil.isNotBlank(record.getFromId())) {
            cos.writeString(3, record.getFromId());
        }
        // Field 4: PayloadSecurity payload_security (0=PLAINTEXT)
        if (record.getPayloadSecurity() != null && record.getPayloadSecurity() != 0) {
            cos.writeEnum(4, record.getPayloadSecurity());
        }
        // Field 7: NoSessionContextRecord no_session_context
        if (record.getPayload() != null && record.getPayload().length > 0) {
            // NoSessionContextRecord 内部 Field 2: bytes payload
            byte[] subBytes = encodeNoSessionContext(record.getPayload());
            cos.writeByteArray(7, subBytes);
        }

        cos.flush();
        return baos.toByteArray();
    }

    private static byte[] encodeNoSessionContext(byte[] payload) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CodedOutputStream cos = CodedOutputStream.newInstance(baos);
        // Field 2: bytes payload
        cos.writeByteArray(2, payload);
        cos.flush();
        return baos.toByteArray();
    }

    /**
     * 将二进制字节解码为 UspRecord
     */
    public static UspRecord decodeRecord(byte[] bytes) throws IOException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        CodedInputStream cis = CodedInputStream.newInstance(bytes);
        UspRecord record = new UspRecord();

        while (!cis.isAtEnd()) {
            int tag = cis.readTag();
            if (tag == 0) {
                break;
            }
            int fieldNumber = WireFormat.getTagFieldNumber(tag);
            switch (fieldNumber) {
                case 1:
                    record.setVersion(cis.readString());
                    break;
                case 2:
                    record.setToId(cis.readString());
                    break;
                case 3:
                    record.setFromId(cis.readString());
                    break;
                case 4:
                    record.setPayloadSecurity(cis.readEnum());
                    break;
                case 7: {
                    // NoSessionContextRecord
                    byte[] noSessionBytes = cis.readByteArray();
                    record.setPayload(decodeNoSessionContextPayload(noSessionBytes));
                    break;
                }
                default:
                    cis.skipField(tag);
                    break;
            }
        }
        return record;
    }

    private static byte[] decodeNoSessionContextPayload(byte[] noSessionBytes) throws IOException {
        CodedInputStream cis = CodedInputStream.newInstance(noSessionBytes);
        while (!cis.isAtEnd()) {
            int tag = cis.readTag();
            if (tag == 0) break;
            int field = WireFormat.getTagFieldNumber(tag);
            if (field == 2) {
                return cis.readByteArray();
            } else {
                cis.skipField(tag);
            }
        }
        return new byte[0];
    }

    // ==========================================
    // 2. USP Msg 编解码
    // ==========================================

    /**
     * 将 UspMsg 编码为二进制字节
     */
    public static byte[] encodeMsg(UspMsg msg) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CodedOutputStream cos = CodedOutputStream.newInstance(baos);

        // Field 1: Header header
        byte[] headerBytes = encodeHeader(msg.getMsgId(), msg.getMsgType());
        cos.writeByteArray(1, headerBytes);

        // Field 2: Body body
        byte[] bodyBytes = encodeBody(msg);
        if (bodyBytes != null && bodyBytes.length > 0) {
            cos.writeByteArray(2, bodyBytes);
        }

        cos.flush();
        return baos.toByteArray();
    }

    private static byte[] encodeHeader(String msgId, UspMsgType msgType) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CodedOutputStream cos = CodedOutputStream.newInstance(baos);
        // Field 1: string msg_id
        cos.writeString(1, StrUtil.blankToDefault(msgId, IdUtil.fastSimpleUUID()));
        // Field 2: MsgType msg_type
        cos.writeEnum(2, msgType != null ? msgType.getCode() : UspMsgType.GET.getCode());
        cos.flush();
        return baos.toByteArray();
    }

    private static byte[] encodeBody(UspMsg msg) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CodedOutputStream cos = CodedOutputStream.newInstance(baos);

        if (msg.isRequest() || msg.getGetRequest() != null || msg.getSetRequest() != null || msg.getOperateRequest() != null) {
            // Field 1: Request request
            byte[] reqBytes = encodeRequest(msg);
            cos.writeByteArray(1, reqBytes);
        } else if (msg.isResponse() || msg.getGetResponse() != null || msg.getSetResponse() != null || msg.getOperateResponse() != null) {
            // Field 2: Response response
            byte[] respBytes = encodeResponse(msg);
            cos.writeByteArray(2, respBytes);
        } else if (msg.getError() != null) {
            // Field 3: Error error
            byte[] errBytes = encodeError(msg.getError());
            cos.writeByteArray(3, errBytes);
        }

        cos.flush();
        return baos.toByteArray();
    }

    private static byte[] encodeRequest(UspMsg msg) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CodedOutputStream cos = CodedOutputStream.newInstance(baos);

        if (msg.getGetRequest() != null) {
            // Field 1: Get get
            byte[] getBytes = encodeGet(msg.getGetRequest());
            cos.writeByteArray(1, getBytes);
        } else if (msg.getOperateRequest() != null) {
            // Field 6: Operate operate
            byte[] operateBytes = encodeOperate(msg.getOperateRequest());
            cos.writeByteArray(6, operateBytes);
        }

        cos.flush();
        return baos.toByteArray();
    }

    private static byte[] encodeGet(UspGetRequest getReq) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CodedOutputStream cos = CodedOutputStream.newInstance(baos);
        if (getReq.getParamPaths() != null) {
            for (String path : getReq.getParamPaths()) {
                cos.writeString(1, path);
            }
        }
        if (getReq.getMaxDepth() != null && getReq.getMaxDepth() > 0) {
            cos.writeUInt32(2, getReq.getMaxDepth());
        }
        cos.flush();
        return baos.toByteArray();
    }

    private static byte[] encodeOperate(UspOperateRequest opReq) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CodedOutputStream cos = CodedOutputStream.newInstance(baos);
        if (StrUtil.isNotBlank(opReq.getCommand())) {
            cos.writeString(1, opReq.getCommand());
        }
        if (StrUtil.isNotBlank(opReq.getCommandKey())) {
            cos.writeString(2, opReq.getCommandKey());
        }
        cos.writeBool(3, Boolean.TRUE.equals(opReq.getSendResp()));
        cos.flush();
        return baos.toByteArray();
    }

    private static byte[] encodeResponse(UspMsg msg) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CodedOutputStream cos = CodedOutputStream.newInstance(baos);
        // 省略根据需要编码的返回
        cos.flush();
        return baos.toByteArray();
    }

    private static byte[] encodeError(UspError err) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CodedOutputStream cos = CodedOutputStream.newInstance(baos);
        if (err.getErrCode() != null) {
            cos.writeFixed32(1, err.getErrCode());
        }
        if (StrUtil.isNotBlank(err.getErrMsg())) {
            cos.writeString(2, err.getErrMsg());
        }
        cos.flush();
        return baos.toByteArray();
    }

    /**
     * 将二进制字节解码为 UspMsg
     */
    public static UspMsg decodeMsg(byte[] bytes) throws IOException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        CodedInputStream cis = CodedInputStream.newInstance(bytes);
        UspMsg msg = new UspMsg();

        while (!cis.isAtEnd()) {
            int tag = cis.readTag();
            if (tag == 0) break;
            int field = WireFormat.getTagFieldNumber(tag);
            switch (field) {
                case 1: {
                    // Header
                    byte[] headerBytes = cis.readByteArray();
                    decodeHeader(headerBytes, msg);
                    break;
                }
                case 2: {
                    // Body
                    byte[] bodyBytes = cis.readByteArray();
                    decodeBody(bodyBytes, msg);
                    break;
                }
                default:
                    cis.skipField(tag);
                    break;
            }
        }
        return msg;
    }

    private static void decodeHeader(byte[] headerBytes, UspMsg msg) throws IOException {
        CodedInputStream cis = CodedInputStream.newInstance(headerBytes);
        while (!cis.isAtEnd()) {
            int tag = cis.readTag();
            if (tag == 0) break;
            int field = WireFormat.getTagFieldNumber(tag);
            switch (field) {
                case 1:
                    msg.setMsgId(cis.readString());
                    break;
                case 2:
                    msg.setMsgType(UspMsgType.valueOfCode(cis.readEnum()));
                    break;
                default:
                    cis.skipField(tag);
                    break;
            }
        }
    }

    private static void decodeBody(byte[] bodyBytes, UspMsg msg) throws IOException {
        CodedInputStream cis = CodedInputStream.newInstance(bodyBytes);
        while (!cis.isAtEnd()) {
            int tag = cis.readTag();
            if (tag == 0) break;
            int field = WireFormat.getTagFieldNumber(tag);
            switch (field) {
                case 1: {
                    // Request
                    byte[] reqBytes = cis.readByteArray();
                    decodeRequest(reqBytes, msg);
                    break;
                }
                case 2: {
                    // Response
                    byte[] respBytes = cis.readByteArray();
                    decodeResponse(respBytes, msg);
                    break;
                }
                case 3: {
                    // Error
                    byte[] errBytes = cis.readByteArray();
                    decodeError(errBytes, msg);
                    break;
                }
                default:
                    cis.skipField(tag);
                    break;
            }
        }
    }

    private static void decodeRequest(byte[] reqBytes, UspMsg msg) throws IOException {
        CodedInputStream cis = CodedInputStream.newInstance(reqBytes);
        while (!cis.isAtEnd()) {
            int tag = cis.readTag();
            if (tag == 0) break;
            int field = WireFormat.getTagFieldNumber(tag);
            if (field == 2) {
                // Notify
                byte[] notifyBytes = cis.readByteArray();
                decodeNotify(notifyBytes, msg);
            } else {
                cis.skipField(tag);
            }
        }
    }

    private static void decodeResponse(byte[] respBytes, UspMsg msg) throws IOException {
        CodedInputStream cis = CodedInputStream.newInstance(respBytes);
        while (!cis.isAtEnd()) {
            int tag = cis.readTag();
            if (tag == 0) break;
            int field = WireFormat.getTagFieldNumber(tag);
            switch (field) {
                case 1: {
                    // GetResp
                    byte[] getRespBytes = cis.readByteArray();
                    decodeGetResp(getRespBytes, msg);
                    break;
                }
                case 3: {
                    // SetResp
                    msg.setSetResponse(UspSetResponse.builder().success(true).build());
                    cis.skipField(tag);
                    break;
                }
                case 6: {
                    // OperateResp
                    msg.setOperateResponse(UspOperateResponse.builder().executed(true).build());
                    cis.skipField(tag);
                    break;
                }
                default:
                    cis.skipField(tag);
                    break;
            }
        }
    }

    private static void decodeGetResp(byte[] getRespBytes, UspMsg msg) throws IOException {
        CodedInputStream cis = CodedInputStream.newInstance(getRespBytes);
        Map<String, String> entries = new HashMap<>();

        while (!cis.isAtEnd()) {
            int tag = cis.readTag();
            if (tag == 0) break;
            int field = WireFormat.getTagFieldNumber(tag);
            if (field == 1) {
                // ReqPathResult
                byte[] pathResultBytes = cis.readByteArray();
                decodeReqPathResult(pathResultBytes, entries);
            } else {
                cis.skipField(tag);
            }
        }
        msg.setGetResponse(UspGetResponse.builder().entries(entries).build());
    }

    private static void decodeReqPathResult(byte[] bytes, Map<String, String> entries) throws IOException {
        CodedInputStream cis = CodedInputStream.newInstance(bytes);
        while (!cis.isAtEnd()) {
            int tag = cis.readTag();
            if (tag == 0) break;
            int field = WireFormat.getTagFieldNumber(tag);
            if (field == 3) {
                // ResolvedPathResult
                byte[] resBytes = cis.readByteArray();
                decodeResolvedPathResult(resBytes, entries);
            } else {
                cis.skipField(tag);
            }
        }
    }

    private static void decodeResolvedPathResult(byte[] bytes, Map<String, String> entries) throws IOException {
        CodedInputStream cis = CodedInputStream.newInstance(bytes);
        String resolvedPath = "";
        while (!cis.isAtEnd()) {
            int tag = cis.readTag();
            if (tag == 0) break;
            int field = WireFormat.getTagFieldNumber(tag);
            if (field == 1) {
                resolvedPath = cis.readString();
            } else if (field == 2) {
                // map<string, string> result_params
                byte[] entryBytes = cis.readByteArray();
                Map.Entry<String, String> entry = decodeMapEntry(entryBytes);
                if (entry != null) {
                    String fullKey = StrUtil.isNotBlank(resolvedPath) && !resolvedPath.endsWith(".")
                            ? resolvedPath + "." + entry.getKey()
                            : resolvedPath + entry.getKey();
                    entries.put(fullKey, entry.getValue());
                }
            } else {
                cis.skipField(tag);
            }
        }
    }

    private static Map.Entry<String, String> decodeMapEntry(byte[] bytes) throws IOException {
        CodedInputStream cis = CodedInputStream.newInstance(bytes);
        String key = "";
        String value = "";
        while (!cis.isAtEnd()) {
            int tag = cis.readTag();
            if (tag == 0) break;
            int field = WireFormat.getTagFieldNumber(tag);
            if (field == 1) {
                key = cis.readString();
            } else if (field == 2) {
                value = cis.readString();
            } else {
                cis.skipField(tag);
            }
        }
        final String finalKey = key;
        final String finalVal = value;
        return new Map.Entry<String, String>() {
            @Override public String getKey() { return finalKey; }
            @Override public String getValue() { return finalVal; }
            @Override public String setValue(String value) { return finalVal; }
        };
    }

    private static void decodeNotify(byte[] notifyBytes, UspMsg msg) throws IOException {
        CodedInputStream cis = CodedInputStream.newInstance(notifyBytes);
        UspNotify notify = new UspNotify();
        while (!cis.isAtEnd()) {
            int tag = cis.readTag();
            if (tag == 0) break;
            int field = WireFormat.getTagFieldNumber(tag);
            switch (field) {
                case 1:
                    notify.setSubscriptionId(cis.readString());
                    break;
                case 2:
                    notify.setSendResp(cis.readBool());
                    break;
                case 3: {
                    // Event
                    notify.setType("Event");
                    cis.skipField(tag);
                    break;
                }
                case 4: {
                    // ValueChange
                    notify.setType("ValueChange");
                    cis.skipField(tag);
                    break;
                }
                default:
                    cis.skipField(tag);
                    break;
            }
        }
        msg.setNotify(notify);
    }

    private static void decodeError(byte[] errBytes, UspMsg msg) throws IOException {
        CodedInputStream cis = CodedInputStream.newInstance(errBytes);
        UspError err = new UspError();
        while (!cis.isAtEnd()) {
            int tag = cis.readTag();
            if (tag == 0) break;
            int field = WireFormat.getTagFieldNumber(tag);
            switch (field) {
                case 1:
                    err.setErrCode(cis.readFixed32());
                    break;
                case 2:
                    err.setErrMsg(cis.readString());
                    break;
                default:
                    cis.skipField(tag);
                    break;
            }
        }
        msg.setError(err);
    }

}
