package com.mz.bus.message;

import com.mz.bus.enums.MsgPayloadType;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;

import java.nio.charset.StandardCharsets;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;

public interface EncodedMessage {
    @Nonnull
    ByteBuf getPayload();

    default String payloadAsString() {
        return this.getPayload().toString(StandardCharsets.UTF_8);
    }

    default JSONObject payloadAsJson() {
        return (JSONObject)JSON.parse(this.payloadAsBytes(), new Feature[0]);
    }

    default JSONArray payloadAsJsonArray() {
        return (JSONArray)JSON.parse(this.payloadAsBytes(), new Feature[0]);
    }

    default byte[] payloadAsBytes() {
        return ByteBufUtil.getBytes(this.getPayload());
    }

    /** @deprecated */
    @Deprecated
    default byte[] getBytes() {
        return ByteBufUtil.getBytes(this.getPayload());
    }

    default byte[] getBytes(int offset, int len) {
        return ByteBufUtil.getBytes(this.getPayload(), offset, len);
    }

    /** @deprecated */
    @Nullable
    @Deprecated
    default MsgPayloadType getPayloadType() {
        return MsgPayloadType.JSON;
    }

    static EmptyMessage empty() {
        return EmptyMessage.INSTANCE;
    }

    static EncodedMessage simple(ByteBuf data) {
        return simple(data, MsgPayloadType.BINARY);
    }

    static EncodedMessage simple(ByteBuf data, MsgPayloadType payloadType) {
        return SimpleMessage.of(data, payloadType);
    }
}
