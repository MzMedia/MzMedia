package org.dromara.modbus;

import org.dromara.modbus.config.ModbusConfig;
import org.dromara.protocol.core.IMessageHandler;
import io.vertx.core.AbstractVerticle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

@Slf4j
public class VertxModbusServer extends AbstractVerticle {

    private final ModbusConfig config;
    private IMessageHandler handler;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public VertxModbusServer(ModbusConfig config) {
        this.config = config;
    }

    public void setHandler(IMessageHandler handler) {
        this.handler = handler;
    }

    @Override
    public void start() throws Exception {

    }

}
