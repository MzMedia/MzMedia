package com.mz.network.codec;

import com.mz.network.client.message.BaseMessage;
import com.mz.network.session.BaseSession;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.Message;

public class WriteHandler extends AbstractProtocolHandler<BaseMessage, Object>{
    @Override
    protected String getTopic() {
        return null;
    }

    @Override
    protected String getMessageType() {
        return null;
    }

    @Override
    public Buffer getBuffer(Object req) {
        return null;
    }

    @Override
    public void write(BaseSession session, Buffer buffer) {

    }

    @Override
    public void handle(Message<BaseMessage> baseMessageMessage) {

    }
}
