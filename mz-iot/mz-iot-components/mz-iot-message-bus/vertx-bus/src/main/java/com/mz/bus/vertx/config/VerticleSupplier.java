package com.mz.bus.vertx.config;

import io.vertx.core.Verticle;
import io.vertx.core.VertxOptions;

import java.util.function.Supplier;

public interface VerticleSupplier extends Supplier<Verticle> {
    default int getInstances() {
        return VertxOptions.DEFAULT_EVENT_LOOP_POOL_SIZE;
    }
}
