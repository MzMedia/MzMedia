package com.mz.bus.vertx.config;

import io.vertx.core.Verticle;

import java.util.function.Supplier;

import static com.mz.bus.vertx.VertxManager.MQTT_EVENT_LOOP_POOL_SIZE;

public interface VerticleSupplier extends Supplier<Verticle> {
    default int getInstances() {
        return MQTT_EVENT_LOOP_POOL_SIZE;
    }
}
