package com.mz.tr069;

import com.mz.protocol.core.IMessageHandler;
import com.mz.tr069.config.Tr069Config;
import io.vertx.core.AbstractVerticle;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VertxTr069Server extends AbstractVerticle {
    private final Tr069Config config;
    private IMessageHandler handler;

    public VertxTr069Server(Tr069Config config) {
        this.config = config;
    }
    public void setHandler(IMessageHandler handler) {
        this.handler = handler;
    }

    @Override
    public void start() throws Exception {

    }

}
