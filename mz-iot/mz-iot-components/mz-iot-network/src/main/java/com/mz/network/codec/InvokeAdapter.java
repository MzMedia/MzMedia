package com.mz.network.codec;

import com.mz.network.session.BaseSession;
import io.vertx.core.buffer.Buffer;

public class InvokeAdapter{
    private ProtocolServerBootstrap bootstrap;

    public InvokeAdapter(ProtocolServerBootstrap bootstrap){
        this.bootstrap = bootstrap;
    }

    public void send(String protocolName, String logicAddress, String messageType, Object param) {
        AbstractProtocolCodec codec = bootstrap.getProtocolCodec(protocolName);
        BaseSession session = codec.getSessionByLogicAddress(logicAddress);
        if (session == null || session.getSocket() == null) {
            throw new RuntimeException("session is not exist or closed");
        }
        AbstractProtocolHandler handler = codec.getHandlerByMessageType(messageType);
        Buffer buffer = handler.getBuffer(param);
        handler.write(session, buffer);
    }

}
