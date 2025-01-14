package org.dromara.bus.core;

public interface Sub<T> {
    void sub(String route, SubCallback<T> handler);
}
