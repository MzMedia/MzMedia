package org.dromara.bus.vertx;

import org.dromara.common.json.utils.JsonUtils;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.json.Json;

public class BeanCodec<T> implements MessageCodec<T, T> {
    private final Class<T> beanType;

    public BeanCodec(Class<T> cls) {
        beanType = cls;
    }

    @Override
    public void encodeToWire(Buffer buffer, T o) {
        String json = Json.encode(o);
        Buffer encoded = Buffer.buffer(json);
        buffer.appendInt(encoded.length());
        buffer.appendBuffer(encoded);
    }

    @Override
    public T decodeFromWire(int pos, Buffer buffer) {
        int length = buffer.getInt(pos);
        pos += 4;
        return Json.decodeValue(buffer.slice(pos, pos + length), beanType);
    }

    @Override
    public T transform(T o) {
        return JsonUtils.parseObject(JsonUtils.toJsonString(o), beanType);
    }

    @Override
    public String name() {
        return beanType.getSimpleName();
    }

    @Override
    public byte systemCodecID() {
        return -1;
    }
}
