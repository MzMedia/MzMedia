package com.mz.bus.mqtt;

import com.mz.bus.core.Pub;
import io.vertx.core.Vertx;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MqtttPub<T> implements Pub<T> {

    @Autowired
    private MqttAsyncClient client;

    @SneakyThrows
    public MqtttPub(Class<T> cls) {

    }

    public void publish(int qos, boolean retained, String topic, String pushMessage) {
        MqttMessage message = new MqttMessage();
        message.setQos(qos);
        message.setRetained(retained);
        message.setPayload(pushMessage.getBytes());
        try {
            IMqttDeliveryToken token = client.publish(topic, message);
            token.waitForCompletion();
            log.info("=>发布主题 topic={}", topic);
        } catch (MqttPersistenceException e) {
            e.printStackTrace();
        } catch (MqttException e) {
            log.error("=>发布主题时发生错误 topic={},message={}", topic, e.getMessage());
        }
    }

    @Override
    public void publish(String route, T msg) {
        publish(1, true, route, (String) msg);
    }
}
