package com.mz.network.client.message.em;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Method {
    //主设备
    BASEINFO(0, "baseinfo"),
    PROPERTY(1, "property"),
    ACTION(2, "action"),
    EVENT(3, "event"),
    REPLY(4, "reply"),
    //子设备
    REGISTER(5, "register"),
    ADD(6, "add"),
    DEL(7, "del"),
    LIST(8, "list"),
    ALL(999, "all");
    private final int code;
    private final String method;
}
