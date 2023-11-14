package com.mz.bus.mqtt;

import com.mz.bus.core.SubCallback;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class MqttCallback implements MqttCallbackExtended {

    @Autowired
    private MqttAsyncClient client;

    @Autowired
    private MqttConnectOptions options;

    private final Map<String, SubCallback> subCallbackMap = new ConcurrentHashMap<>();

    public void put(String route, SubCallback handler) {
        this.subCallbackMap.put(route,handler);
    }

    @Override
    public void connectComplete(boolean b, String s) {

    }

    @Override
    public void connectionLost(Throwable throwable) {
        int count = 1;
        // int sleepTime = 0;
        boolean willConnect = true;
        while (willConnect) {
            try {
                Thread.sleep(1000);
                log.debug("=>连接[{}]断开，尝试重连第{}次", this.client.getServerURI(), count++);
                this.client.connect(this.options);
                log.debug("=>重连成功");
                willConnect = false;
            } catch (Exception e) {
                log.error("=>重连异常", e);
            }
        }
    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        SubCallback callback = subCallbackMap.get(topic);
        if (callback != null) {
            callback.callback(mqttMessage);
        }

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }
}
