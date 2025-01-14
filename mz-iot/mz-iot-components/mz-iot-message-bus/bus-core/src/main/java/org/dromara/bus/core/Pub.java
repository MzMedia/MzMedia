package org.dromara.bus.core;

public interface Pub<T> {
    void publish(String route, T msg);
}
