package com.mz.bus.enums;

public enum MsgPayloadType {
    JSON,
    STRING,
    BINARY,
    HEX,
    UNKNOWN;

    private MsgPayloadType() {
    }

    public static MsgPayloadType of(String of) {
        MsgPayloadType[] var1 = values();
        for (MsgPayloadType value : var1) {
            if (value.name().equalsIgnoreCase(of)) {
                return value;
            }
        }
        return UNKNOWN;
    }
}
