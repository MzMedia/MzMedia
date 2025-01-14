package org.dromara.bus.vertx.config;

import io.vertx.core.Verticle;

import java.util.function.Supplier;

import static org.dromara.bus.vertx.VertxManager.MQTT_EVENT_LOOP_POOL_SIZE;

public interface VerticleSupplier extends Supplier<Verticle> {
    default int getInstances() {
        return MQTT_EVENT_LOOP_POOL_SIZE;
    }
}
