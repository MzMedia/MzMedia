package org.dromara.bus.mqtt.config;

import lombok.Data;

@Data
public class MqttClientOptions {
    private String hostUrl;
    private String username;
    private String password;
    private int timeout;
    private String clientId;
    private int keepalive;
}
