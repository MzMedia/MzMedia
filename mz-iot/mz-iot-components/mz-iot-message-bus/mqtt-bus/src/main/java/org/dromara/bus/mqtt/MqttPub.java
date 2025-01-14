package org.dromara.bus.mqtt;

import org.dromara.bus.core.Pub;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MqttPub<T> implements Pub<T> {

    @Autowired
    private MqttAsyncClient client;

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
