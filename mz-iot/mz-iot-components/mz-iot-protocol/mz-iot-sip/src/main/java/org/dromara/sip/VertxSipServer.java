package org.dromara.sip;

import org.dromara.protocol.core.IMessageHandler;
import org.dromara.sip.config.SipConfig;
import io.vertx.core.AbstractVerticle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

@Slf4j
public class VertxSipServer extends AbstractVerticle {

    private final SipConfig config;
    private IMessageHandler handler;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public VertxSipServer(SipConfig config) {
        this.config = config;
    }

    public void setHandler(IMessageHandler handler) {
        this.handler = handler;
    }

    @Override
    public void start() throws Exception {

    }

}
