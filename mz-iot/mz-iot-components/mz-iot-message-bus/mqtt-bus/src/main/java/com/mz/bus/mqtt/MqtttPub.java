package com.mz.bus.mqtt;

import com.mz.bus.core.Pub;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MqtttPub<T> implements Pub<T> {
    @Value("${spring.mqtt.host-url}")
    private String hostUrl;
    @Value("${spring.mqtt.username}")
    private String username;
    @Value("${spring.mqtt.password}")
    private String password;
    @Value("${spring.mqtt.timeout}")
    private int timeout;
    @Value("${spring.mqtt.keepalive}")
    private int keepalive;
    @Resource
    private MqttSub mqttCallBack;

    private MqttConnectOptions options;
    private MqttAsyncClient client;
    private boolean isConnected = false;

    public synchronized void initialize() {
        try {
            setOptions();
            createClient();
            while (!client.isConnected()) {
                IMqttToken token = client.connect(options);
                if(token != null && token.isComplete()) {
                    log.debug("=>message bus MQTT客户端启动成功");
                    this.isConnected = true;
                    break;
                }
                log.debug("=>mqtt客户端连接中...");
                Thread.sleep(20000);
            }
        } catch (MqttException ex) {
            log.error("=>message bus MQTT客户端初始化异常", ex);
        } catch (Exception e) {
            log.error("=>连接MQTT服务器异常", e);
            this.isConnected = false;
        }
    }

    public boolean isConnected() {
        return this.isConnected;
    }

    private void createClient() {
        try {
            if (client == null) {
                /*host为主机名，clientId是连接MQTT的客户端ID*/
                client = new MqttAsyncClient(hostUrl, getClientId(), new MemoryPersistence());
                //设置回调函数
                client.setCallback(mqttCallBack);
                mqttCallBack.setClient(client);
                mqttCallBack.setOptions(this.options);
                mqttCallBack.setEnabled(true);
            }
        } catch (Exception e) {
            log.error("=>mqtt客户端创建错误");
        }
    }

    /**
     * 设置连接属性
     */
    private void setOptions() {
        if (options != null) {
            options = null;
        }
        options = new MqttConnectOptions();
        options.setConnectionTimeout(timeout);
        options.setKeepAliveInterval(keepalive);
        options.setUserName(username);
        options.setPassword(password.toCharArray());
        //设置自动重新连接
        options.setAutomaticReconnect(true);
            /*设置为false，断开连接，不清除session，重连后还是原来的session
              保留订阅的主题，能接收离线期间的消息*/
        options.setCleanSession(true);
    }

    /**
     * 断开与mqtt的连接
     */
    public synchronized void disconnect() {
        //判断客户端是否null 是否连接
        if (client != null && client.isConnected()) {
            try {
                IMqttToken token = client.disconnect();
                token.waitForCompletion();
            } catch (MqttException e) {
                log.error("=>断开mqtt连接发生错误 message={}", e.getMessage());
                //throw new ServiceException("断开mqtt连接发生错误" + e.getMessage());
            }
        }
        client = null;
    }

    /**
     * 重新连接MQTT
     */
    public synchronized void refresh() {
        disconnect();
        initialize();
    }

    /**
     * 拼接客户端id
     */
    public final String getClientId() {
        return null;
    }

    /**
     * 发布
     *
     * @param qos         连接方式
     * @param retained    是否保留
     * @param topic       主题
     * @param pushMessage 消息体
     */
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
