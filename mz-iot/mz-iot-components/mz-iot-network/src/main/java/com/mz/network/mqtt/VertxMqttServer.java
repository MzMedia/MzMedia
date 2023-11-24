package com.mz.network.mqtt;

import com.alibaba.fastjson.JSON;
import com.mz.network.authority.DeviceAuthorityService;
import com.mz.network.client.Client;
import com.mz.network.client.ClientRepository;
import com.mz.network.client.message.ClientMessage;
import com.mz.network.core.Topics;
import com.mz.network.events.CommandReplyEvent;
import com.mz.network.events.DeviceReportEvent;
import io.netty.handler.codec.mqtt.MqttConnectReturnCode;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.buffer.Buffer;
import io.vertx.mqtt.*;
import io.vertx.mqtt.messages.MqttPublishMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Slf4j
public class VertxMqttServer extends AbstractVerticle {

    @Autowired
    private ClientRepository clientRepository;

    @Value("${vertx.service-id}")
    private String serviceId;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private MqttServerOptions mqttServerOptions;

    @Autowired
    private DeviceAuthorityService authorityService;

    private final Map<String, MqttEndpoint> subscribers = new ConcurrentHashMap<>();


    @Override
    public void start() throws Exception {
        MqttServer mqttServer = MqttServer.create(vertx, mqttServerOptions);
        mqttServer.endpointHandler(mqttEndpoint -> {
            String clientId = mqttEndpoint.clientIdentifier();
            log.debug("接收到MQTT客户端[{}]消息", clientId);
            //执行创建链接
            doConnect(mqttEndpoint);
        }).listen(result -> {
            if (result.succeeded()) {
                int port = mqttServer.actualPort();
                log.debug("MQTT server started on port {}", port);
                simulateClients();
            } else {
                log.warn("MQTT server start failed", result.cause());
            }
        });
    }

    protected void doConnect(MqttEndpoint endpoint) {
        if (endpoint.auth() == null) {
            endpoint.reject(MqttConnectReturnCode.CONNECTION_REFUSED_NOT_AUTHORIZED);
            return;
        }
        String userName = endpoint.auth().userName();
        String passWord = endpoint.auth().password();

        if (authorityService.verification(endpoint.clientIdentifier(), userName, passWord)) {
            log.debug("MQTT客户端:{}认证通过", endpoint.clientIdentifier());
            acceptConnect(endpoint);
        } else {
            log.warn("客户端[{}]用户名密码错误", endpoint.clientIdentifier());
            endpoint.reject(MqttConnectReturnCode.CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD);
        }
    }

    protected void acceptConnect(MqttEndpoint endpoint) {
        String clientId = endpoint.clientIdentifier();
        MqttClienttest client = new MqttClienttest(endpoint);

        endpoint.accept(false)
                .closeHandler(v -> {
                    log.debug("[{}] closed", clientId);
                    Client old = clientRepository.getClient(clientId);
                    if (old == client) {
                        clientRepository.unregister(clientId);
                    } else {
                        log.debug("client {} is unregistered", client);
                    }
                })
                .subscribeHandler(subscribe -> {
                    List<MqttQoS> grantedQosLevels = new ArrayList<>();
                    for (MqttTopicSubscription s : subscribe.topicSubscriptions()) {
                        log.info("[{}] Subscription for {} with QoS {}", clientId, s.topicName(), s.qualityOfService());
                        grantedQosLevels.add(s.qualityOfService());
                        subscribers.put(s.topicName(), endpoint);
                    }
                    // ack the subscriptions request
                    endpoint.subscribeAcknowledge(subscribe.messageId(), grantedQosLevels);

                    // specifing handlers for handling QoS 1 and 2
                    endpoint.publishAcknowledgeHandler(messageId -> log.info("[{}] Received ack for message = {}", clientId, messageId))
                            .publishReceivedHandler(endpoint::publishRelease)
                            .publishCompletionHandler(messageId -> log.info("[{}] Received ack for message = {}", clientId, messageId));

                })
                .unsubscribeHandler(unsubscribe -> {
                    unsubscribe.topics().forEach(topicName -> {
                        log.info("[{}] Unsubscription for {}", clientId, topicName);
                        // Remove the endpoint when a client unsubscribes
                        subscribers.remove(topicName);
                    });
                    // ack the subscriptions request
                    endpoint.unsubscribeAcknowledge(unsubscribe.messageId());
                })
                .disconnectHandler(v -> log.info("[{}] Received disconnect from client", clientId))
                .exceptionHandler(e -> log.error(clientId, e))
                .publishHandler(message -> {
                    //设备推送了消息
                    String topicName = message.topicName();
                    Buffer buffer = message.payload();
                    String payload = buffer.toString();

                    log.info("接受到客户端消息推送:[{}] payload [{}] with QoS [{}]", topicName, payload, message.qosLevel());
                    if (message.qosLevel() == MqttQoS.AT_LEAST_ONCE) {
                        endpoint.publishAcknowledge(message.messageId());
                    } else if (message.qosLevel() == MqttQoS.EXACTLY_ONCE) {
                        endpoint.publishReceived(message.messageId());
                    }

                    //往订阅的客户端发送消息
                    handlePublish(message);
                    //平台物模型处理
                    try {
                        ClientMessage event = null;
                        //目前仅支持reply和report的topic
                        if (Topics.reply.equals(topicName)) {
                            event = JSON.parseObject(payload, CommandReplyEvent.class);
                        } else if (Topics.report.equals(topicName)) {
                            event = JSON.parseObject(payload, DeviceReportEvent.class);
                        }
                        if (null != event) {
                            event.setClientId(clientId);
                            //发布事件到spring
                            eventPublisher.publishEvent(event);
                        } else {
                            log.warn("不支持的topic:{} => {}", topicName, payload);
                        }
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                })
                .publishReleaseHandler(messageId -> {
                    log.debug("complete message :{}", messageId);
                    endpoint.publishComplete(messageId);
                });
        //注册设备
        clientRepository.register(client);
    }
    private void handlePublish(MqttPublishMessage message) {
        // Handle incoming publish messages
        String topic = message.topicName();
        String payload = message.payload().toString();
        int qos = message.qosLevel().value();

        System.out.println("Received message on [" + topic + "] payload [" + payload + "] with QoS " + qos);

        // Forward the message to subscribers matching the topic
        subscribers.forEach((subscribedTopic, subscriberEndpoint) -> {
            if (topicMatches(subscribedTopic, topic)) {
                // Handle different QoS levels
                switch (qos) {
                    case 0:
                        // QoS 0: At most once delivery, no acknowledgment needed
                        subscriberEndpoint.publish(topic, message.payload(), message.qosLevel(), message.isDup(), false);
                        System.out.println("Message forwarded to [" + subscribedTopic + "] with QoS 0");
                        break;
                    case 1:
                    case 2:
                        // QoS 1 and QoS 2: Acknowledgment needed
                        subscriberEndpoint.publish(topic, message.payload(), message.qosLevel(), message.isDup(), false,
                            publishResult -> handlePublishResult(publishResult, subscribedTopic, qos));
                        break;
                    default:
                        System.err.println("Unsupported QoS level: " + qos);
                }
            }
        });
    }

    private void handlePublishResult(AsyncResult<Integer> publishResult, String topic, int qos) {
        if (publishResult.succeeded()) {
            System.out.println("Message forwarded to subscribers of [" + topic + "] with QoS " + qos);
        } else {
            System.err.println("Failed to forward message to [" + topic + "] with QoS " + qos +
                ": " + publishResult.cause().getMessage());
        }
    }

    private void simulateClients() {
        // Simulate a subscriber
        MqttClientOptions subscriberOptions = new MqttClientOptions()
            .setClientId("subscriber")
            .setUsername("username")
            .setPassword("password")
            .setCleanSession(true);

        MqttClient subscriber = MqttClient.create(vertx, subscriberOptions);

        subscriber.connect(1883, "localhost", ar -> {
            if (ar.succeeded()) {
                System.out.println("Subscriber connected to MQTT server");

                // Simulate subscribing to a topic with wildcards
                String topicWithWildcard = "example/#";
                int qos = 1;

                subscriber.subscribe(topicWithWildcard, qos, subscribeResult -> {
                    if (subscribeResult.succeeded()) {
                        System.out.println("Subscriber subscribed to [" + topicWithWildcard + "] with QoS " + qos);
                    } else {
                        System.err.println("Failed to subscribe to [" + topicWithWildcard + "]: " +
                            subscribeResult.cause().getMessage());
                    }
                });
            } else {
                System.err.println("Error connecting to MQTT server: " + ar.cause().getMessage());
            }
        });

        // Simulate a publisher
        MqttClientOptions publisherOptions = new MqttClientOptions()
            .setClientId("publisher")
            .setUsername("username")
            .setPassword("password")
            .setCleanSession(true);

        MqttClient publisher = MqttClient.create(vertx, publisherOptions);

        publisher.connect(1883, "localhost", ar -> {
            if (ar.succeeded()) {
                System.out.println("Publisher connected to MQTT server");

                // Simulate publishing messages to different topics
                String topic1 = "example/topic1";
                String topic2 = "example/topic2";
                Buffer payload = Buffer.buffer("Hello Vert.x MQTT Server");
                MqttQoS qos = MqttQoS.valueOf(1);

                publisher.publish(topic1, payload, qos, false, false, publishResult -> {
                    if (publishResult.succeeded()) {
                        System.out.println("Message published to [" + topic1 + "] with QoS " + qos);
                    } else {
                        System.err.println("Failed to publish message to [" + topic1 + "]: " +
                            publishResult.cause().getMessage());
                    }
                });

                publisher.publish(topic2, payload, qos, false, false, publishResult -> {
                    if (publishResult.succeeded()) {
                        System.out.println("Message published to [" + topic2 + "] with QoS " + qos);
                    } else {
                        System.err.println("Failed to publish message to [" + topic2 + "]: " +
                            publishResult.cause().getMessage());
                    }

                    // Disconnect after publishing
                    publisher.disconnect();
                });
            } else {
                System.err.println("Error connecting to MQTT server: " + ar.cause().getMessage());
            }
        });
    }

    private boolean topicMatches(String subscribedTopic, String actualTopic) {
        String[] subscribedParts = subscribedTopic.split("/");
        String[] actualParts = actualTopic.split("/");

        if (subscribedParts.length != actualParts.length) {
            return false;
        }

        for (int i = 0; i < subscribedParts.length; i++) {
            if (!subscribedParts[i].equals("+") && !subscribedParts[i].equals(actualParts[i])) {
                return false;
            }
        }

        return true;
    }

}
