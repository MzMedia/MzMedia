package org.dromara.network.client;


import org.dromara.network.core.IotCommand;


public interface Client {

    String getId();

    String getClientId();

    long lastPingTime();

    void send(String topic, IotCommand command);

    void close();

    void ping();

}
