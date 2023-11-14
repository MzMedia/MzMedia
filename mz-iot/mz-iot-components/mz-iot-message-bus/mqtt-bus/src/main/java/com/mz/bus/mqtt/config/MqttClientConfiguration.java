package com.mz.bus.mqtt.config;

import com.mz.bus.core.Pub;
import com.mz.bus.core.Sub;
import com.mz.bus.core.SubCallback;
import com.mz.bus.message.BaseMessage;
import com.mz.bus.mqtt.MqttSub;
import com.mz.bus.mqtt.MqtttPub;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhouhao
 * @since 1.0.0
 */

@Slf4j
@Configuration
public class MqttClientConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "mqttClient")
    public MqttClientOptions MqttClientOptions() {
        return new MqttClientOptions();
    }

    @Bean
    @DependsOn("MqttClientOptions")
    public MqttConnectOptions MqttConnectOptions() {
        MqttClientOptions clientOptions = this.MqttClientOptions();
        MqttConnectOptions options = new MqttConnectOptions();
        options.setConnectionTimeout(clientOptions.getTimeout());
        options.setKeepAliveInterval(clientOptions.getKeepalive());
        options.setUserName(clientOptions.getUsername());
        options.setPassword(clientOptions.getPassword().toCharArray());
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        return new MqttConnectOptions();
    }

    @ConditionalOnMissingBean
    @Bean
    public Pub<BaseMessage> getBaseMessageProducer() {
        return new MqtttPub<>(BaseMessage.class);
    }

    @ConditionalOnMissingBean
    @Bean
    public Sub<BaseMessage> getBaseMessageConsumer() {
        return new MqttSub<>(BaseMessage.class);
    }

    @ConditionalOnMissingBean
    @Bean
    @DependsOn("MqttConnectOptions")
    public MqttAsyncClient mqttAsyncClient() throws MqttException {
        MqttClientOptions options = this.MqttClientOptions();
        MqttAsyncClient client = new MqttAsyncClient(options.getHostUrl(), options.getClientId(), new MemoryPersistence());
        try {
            while (!client.isConnected()) {
                IMqttToken token = client.connect(this.MqttConnectOptions());
                if(token != null && token.isComplete()) {
                    log.debug("=>message bus MQTT客户端启动成功");
                    break;
                }
                log.debug("=>mqtt客户端连接中...");
                Thread.sleep(20000);
            }
        } catch (MqttException ex) {
            log.error("=>message bus MQTT客户端初始化异常", ex);
        } catch (Exception e) {
            log.error("=>连接MQTT服务器异常", e);
        }

        return client;
    }
}
