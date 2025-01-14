package org.dromara.network.client;

public interface ClientMessageHandler<T> {
    void handle(T message);
}
