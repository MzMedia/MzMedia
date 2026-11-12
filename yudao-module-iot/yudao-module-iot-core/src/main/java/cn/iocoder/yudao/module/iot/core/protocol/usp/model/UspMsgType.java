package cn.iocoder.yudao.module.iot.core.protocol.usp.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * TR-369 USP 消息类型枚举 (Header.MsgType)
 *
 * @author 芋道源码
 */
@Getter
@AllArgsConstructor
public enum UspMsgType {

    ERROR(0, "ERROR"),
    GET(1, "GET"),
    GET_RESP(2, "GET_RESP"),
    NOTIFY(3, "NOTIFY"),
    SET(4, "SET"),
    SET_RESP(5, "SET_RESP"),
    OPERATE(6, "OPERATE"),
    OPERATE_RESP(7, "OPERATE_RESP"),
    ADD(8, "ADD"),
    ADD_RESP(9, "ADD_RESP"),
    DELETE(10, "DELETE"),
    DELETE_RESP(11, "DELETE_RESP"),
    GET_SUPPORTED_DM(12, "GET_SUPPORTED_DM"),
    GET_SUPPORTED_DM_RESP(13, "GET_SUPPORTED_DM_RESP"),
    GET_INSTANCES(14, "GET_INSTANCES"),
    GET_INSTANCES_RESP(15, "GET_INSTANCES_RESP"),
    NOTIFY_RESP(16, "NOTIFY_RESP");

    private final int code;
    private final String name;

    public static UspMsgType valueOfCode(int code) {
        return Arrays.stream(values())
                .filter(item -> item.getCode() == code)
                .findFirst()
                .orElse(ERROR);
    }

}
