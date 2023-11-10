package com.mz.bus.mqtt;

import com.mz.bus.core.Sub;
import com.mz.bus.core.SubCallback;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@Data
@NoArgsConstructor
public class MqttSub<T> implements Sub<T>, MqttCallbackExtended {
    private MqttAsyncClient client;
    private MqttConnectOptions options;
    private Boolean enabled;
    private static final Map<String, SubCallback> subCallbackMap = new ConcurrentHashMap<>();

    public MqttSub(MqttAsyncClient client, MqttConnectOptions options,Boolean enabled) {
        this.client = client;
        this.options = options;
        this.enabled = enabled;
    }
    @Override
    public void connectComplete(boolean b, String s) {

    }

    @Override
    public void connectionLost(Throwable throwable) {
        // 连接丢失后，一般在这里面进行重连
        log.debug("=>mqtt 连接丢失", throwable);
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
    public void messageArrived(String topic, MqttMessage msg) throws Exception {
        SubCallback callback = subCallbackMap.get(topic);
        if (callback != null) {
            callback.callback(msg);
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }

    @Override
    public void sub(String route, SubCallback<T> handler) {
        subCallbackMap.put(route, handler);
    }
}
