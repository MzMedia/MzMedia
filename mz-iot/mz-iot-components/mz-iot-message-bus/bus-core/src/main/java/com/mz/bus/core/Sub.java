package com.mz.bus.core;

public interface Sub<T> {
    void sub(String route, SubCallback<T> handler);
}
