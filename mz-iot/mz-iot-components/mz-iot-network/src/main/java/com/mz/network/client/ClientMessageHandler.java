package com.mz.network.client;

public interface ClientMessageHandler<T> {
    void handle(T message);
}
