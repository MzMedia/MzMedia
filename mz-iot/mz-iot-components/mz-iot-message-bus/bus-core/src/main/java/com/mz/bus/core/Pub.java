package com.mz.bus.core;

public interface Pub<T> {
    void publish(String route, T msg);
}
