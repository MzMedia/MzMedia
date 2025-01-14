package org.dromara.bus.vertx;

import io.vertx.core.Vertx;

public class VertxManager {
    private static final Vertx INSTANCE = Vertx.vertx();
    public static final int MQTT_EVENT_LOOP_POOL_SIZE = 2;
    public static Vertx getVertx() {
        return INSTANCE;
    }
}
