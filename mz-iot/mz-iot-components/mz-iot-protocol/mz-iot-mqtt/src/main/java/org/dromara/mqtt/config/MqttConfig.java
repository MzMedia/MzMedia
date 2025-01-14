package org.dromara.mqtt.config;

import lombok.Data;

@Data
public class MqttConfig {
    private int port;
    private String sslKey;
    private String sslCert;
    private boolean ssl;
    private boolean useWebSocket;
}
