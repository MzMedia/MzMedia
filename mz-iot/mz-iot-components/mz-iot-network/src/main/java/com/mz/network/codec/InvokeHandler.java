package com.mz.network.codec;

import com.mz.network.session.BaseSession;
import io.vertx.core.buffer.Buffer;

public interface InvokeHandler<R> {
    /**
     * 根据传入参数获取buffer
     * @param req
     * @return
     */
    Buffer getBuffer(R req);

    /**
     * 下发消息
     * @param session
     * @param buffer
     */
    void write(BaseSession session, Buffer buffer);
}
