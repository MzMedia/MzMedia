package org.dromara.mqtt;

import org.dromara.network.client.Client;
import org.dromara.network.core.IotCommand;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.buffer.Buffer;
import io.vertx.mqtt.MqttEndpoint;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class MqttClienttest implements Client {

    private MqttEndpoint endpoint;

    private volatile long lastPingTime = System.currentTimeMillis();

    public MqttClienttest(MqttEndpoint endpoint) {
        endpoint.pingHandler(r -> ping());
        this.endpoint = endpoint;
    }

    @Override
    public String getId() {
        return getClientId();
    }

    @Override
    public String getClientId() {
        return endpoint.clientIdentifier();
    }

    @Override
    public long lastPingTime() {
        return lastPingTime;
    }

    @Override
    public void send(String topic, IotCommand command) {
        endpoint.publish(topic, Buffer.buffer(command.toString()), MqttQoS.AT_MOST_ONCE, false, false);
    }

    @Override
    public void close() {
        if (endpoint.isConnected()) {
            endpoint.close();
        }

    }

    @Override
    public void ping() {
        log.debug("mqtt client[{}] ping", getClientId());
        lastPingTime = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "MQTT Client[" + getClientId() + "]";
    }
}
