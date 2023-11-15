package com.mz.bus.mqtt.config;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;


@Slf4j
@Configuration
public class MqttClientConfiguration {
    @Bean
    @ConfigurationProperties(prefix = "mqttclient")
    public MqttClientOptions MqttClientOptions() {
        return new MqttClientOptions();
    }

    @Bean
    @DependsOn("MqttClientOptions")
    public MqttConnectOptions MqttConnectOptions(MqttClientOptions clientOptions) {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setConnectionTimeout(clientOptions.getTimeout());
        options.setKeepAliveInterval(clientOptions.getKeepalive());
        options.setUserName(clientOptions.getUsername());
        options.setPassword(clientOptions.getPassword().toCharArray());
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        return options;
    }
}
