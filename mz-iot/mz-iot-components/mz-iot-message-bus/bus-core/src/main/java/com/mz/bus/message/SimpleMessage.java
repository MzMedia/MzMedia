package com.mz.bus.message;

import com.mz.bus.enums.MsgPayloadType;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;

import java.beans.ConstructorProperties;
import java.nio.charset.StandardCharsets;

public class SimpleMessage implements EncodedMessage {
    private final ByteBuf payload;
    private final MsgPayloadType payloadType;

    public static SimpleMessage of(ByteBuf byteBuf, MsgPayloadType payloadType) {
        return new SimpleMessage(byteBuf, payloadType);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (ByteBufUtil.isText(this.payload, StandardCharsets.UTF_8)) {
            builder.append(this.payload.toString(StandardCharsets.UTF_8));
        } else {
            ByteBufUtil.appendPrettyHexDump(builder, this.payload);
        }

        return builder.toString();
    }

    @ConstructorProperties({"payload", "payloadType"})
    public SimpleMessage(ByteBuf payload, MsgPayloadType payloadType) {
        this.payload = payload;
        this.payloadType = payloadType;
    }

    public ByteBuf getPayload() {
        return this.payload;
    }

    public MsgPayloadType getPayloadType() {
        return this.payloadType;
    }
}
