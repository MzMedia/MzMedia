package cn.iocoder.yudao.module.iot.core.protocol.usp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TR-369 USP Msg (业务层消息)
 *
 * 对应 Broadband Forum usp-msg-1-3.proto
 *
 * @author 芋道源码
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UspMsg {

    /**
     * 消息唯一标识 (UUID)
     */
    private String msgId;

    /**
     * 消息类型枚举
     */
    private UspMsgType msgType;

    // ========== Requests ==========

    private UspGetRequest getRequest;

    private UspSetRequest setRequest;

    private UspOperateRequest operateRequest;

    // ========== Responses ==========

    private UspGetResponse getResponse;

    private UspSetResponse setResponse;

    private UspOperateResponse operateResponse;

    // ========== Notifications & Errors ==========

    private UspNotify notify;

    private UspError error;

    /**
     * @return 是否为请求类型消息
     */
    public boolean isRequest() {
        return msgType == UspMsgType.GET ||
               msgType == UspMsgType.SET ||
               msgType == UspMsgType.OPERATE ||
               msgType == UspMsgType.ADD ||
               msgType == UspMsgType.DELETE ||
               msgType == UspMsgType.GET_SUPPORTED_DM ||
               msgType == UspMsgType.GET_INSTANCES;
    }

    /**
     * @return 是否为响应类型消息
     */
    public boolean isResponse() {
        return msgType == UspMsgType.GET_RESP ||
               msgType == UspMsgType.SET_RESP ||
               msgType == UspMsgType.OPERATE_RESP ||
               msgType == UspMsgType.ADD_RESP ||
               msgType == UspMsgType.DELETE_RESP ||
               msgType == UspMsgType.GET_SUPPORTED_DM_RESP ||
               msgType == UspMsgType.GET_INSTANCES_RESP ||
               msgType == UspMsgType.NOTIFY_RESP;
    }

    /**
     * @return 是否为错误消息
     */
    public boolean isError() {
        return msgType == UspMsgType.ERROR || error != null;
    }

}
