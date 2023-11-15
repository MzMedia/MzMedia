package com.mz.bus.mqtt.config;

import com.mz.bus.mqtt.MqttCallback;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;



@Slf4j
@Configuration
public class InitConfig {
    @Autowired
    private MqttCallback mqttCallBack;

    @ConditionalOnMissingBean
    @Bean
    @DependsOn("MqttConnectOptions")
    public MqttAsyncClient mqttAsyncClient(MqttClientOptions clientOptions, MqttConnectOptions ConnectOptions) throws MqttException {
        MqttAsyncClient client = new MqttAsyncClient(clientOptions.getHostUrl(), clientOptions.getClientId(), new MemoryPersistence());
        mqttCallBack.setClient(client);
        mqttCallBack.setOptions(ConnectOptions);
        client.setCallback(mqttCallBack);
        try {
            while (!client.isConnected()) {
                IMqttToken token = client.connect(ConnectOptions);
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
