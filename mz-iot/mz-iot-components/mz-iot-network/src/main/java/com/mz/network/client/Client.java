package com.mz.network.client;


import com.mz.network.core.IotCommand;


public interface Client {

    String getId();

    String getClientId();

    long lastPingTime();

    void send(String topic, IotCommand command);

    void close();

    void ping();

}
