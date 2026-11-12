package cn.iocoder.yudao.module.iot.gateway.protocol.usp;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.iocoder.yudao.module.iot.core.enums.IotDeviceMessageMethodEnum;
import cn.iocoder.yudao.module.iot.core.enums.IotProtocolTypeEnum;
import cn.iocoder.yudao.module.iot.core.enums.device.IotDeviceStateEnum;
import cn.iocoder.yudao.module.iot.core.messagebus.core.IotMessageBus;
import cn.iocoder.yudao.module.iot.core.mq.message.IotDeviceMessage;
import cn.iocoder.yudao.module.iot.core.protocol.mapper.Tr181ThingModelMapper;
import cn.iocoder.yudao.module.iot.core.protocol.usp.builder.UspMessageBuilder;
import cn.iocoder.yudao.module.iot.core.protocol.usp.codec.UspProtobufCodec;
import cn.iocoder.yudao.module.iot.core.protocol.usp.model.UspMsg;
import cn.iocoder.yudao.module.iot.core.protocol.usp.model.UspMsgType;
import cn.iocoder.yudao.module.iot.core.protocol.usp.model.UspRecord;
import cn.iocoder.yudao.module.iot.core.topic.state.IotDeviceStateUpdateReqDTO;
import cn.iocoder.yudao.module.iot.core.util.IotDeviceMessageUtils;
import cn.iocoder.yudao.module.iot.gateway.config.IotGatewayProperties;
import cn.iocoder.yudao.module.iot.gateway.protocol.IotProtocol;
import cn.iocoder.yudao.module.iot.gateway.service.device.IotDeviceService;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.mqtt.MqttEndpoint;
import io.vertx.mqtt.MqttServer;
import io.vertx.mqtt.MqttServerOptions;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * TR-369 (USP) over MQTT 协议实现
 *
 * 采用 Vert.x 内嵌 MQTT 服务实现 USP MTP (Message Transfer Protocol)
 *
 * @author 芋道源码
 */
@Slf4j
public class IotUspMqttProtocol implements IotProtocol {

    private final IotGatewayProperties.ProtocolProperties properties;

    @Getter
    private final String serverId;

    @Getter
    private volatile boolean running = false;

    @Getter
    private Vertx vertx;
    private MqttServer mqttServer;

    private final IotUspConfig config;
    private final Map<String, MqttEndpoint> clientEndpoints = new ConcurrentHashMap<>();
    private final Map<String, CompletableFuture<UspMsg>> pendingRpcFutures = new ConcurrentHashMap<>();

    private IotUspDownstreamSubscriber downstreamSubscriber;

    public IotUspMqttProtocol(IotGatewayProperties.ProtocolProperties properties) {
        this.properties = properties;
        this.serverId = IotDeviceMessageUtils.generateServerId(properties.getPort());
        this.config = properties.getUsp() != null ? properties.getUsp() : new IotUspConfig();
    }

    @Override
    public String getId() {
        return properties.getId();
    }

    @Override
    public IotProtocolTypeEnum getType() {
        return IotProtocolTypeEnum.TR369_MQTT;
    }

    @Override
    public void start() {
        if (running) {
            log.warn("[start][TR-369 USP MQTT 协议 {} 已经在运行中]", getId());
            return;
        }

        this.vertx = Vertx.vertx();

        IotMessageBus messageBus = SpringUtil.getBean(IotMessageBus.class);
        IotDeviceService deviceService = SpringUtil.getBean(IotDeviceService.class);

        // 1. 初始化下行消息订阅者
        this.downstreamSubscriber = new IotUspDownstreamSubscriber(
                this, messageBus, deviceService, this::publishToAgent, config.getControllerEndpointId());
        downstreamSubscriber.start();

        // 2. 启动 Vert.x MQTT 服务
        MqttServerOptions options = new MqttServerOptions();
        options.setPort(properties.getPort());
        this.mqttServer = MqttServer.create(vertx, options);

        mqttServer.endpointHandler(this::handleEndpoint);

        mqttServer.listen().onComplete(res -> {
            if (res.succeeded()) {
                running = true;
                log.info("[start][TR-369 USP MQTT 服务启动成功，端口：{}]", properties.getPort());
            } else {
                running = false;
                log.error("[start][TR-369 USP MQTT 服务启动失败，端口：{}]", properties.getPort(), res.cause());
            }
        });
    }

    private void handleEndpoint(MqttEndpoint endpoint) {
        String clientIdentifier = endpoint.clientIdentifier();
        log.info("[handleEndpoint][USP Agent 已连接, ClientId: {}]", clientIdentifier);

        clientEndpoints.put(clientIdentifier, endpoint);
        endpoint.accept(false);

        // 处理订阅
        endpoint.subscribeHandler(subscribe -> {
            endpoint.subscribeAcknowledge(subscribe.messageId(), Arrays.asList(MqttQoS.AT_LEAST_ONCE));
            log.info("[handleEndpoint][USP Agent 订阅成功, ClientId: {}, Topics: {}]",
                    clientIdentifier, subscribe.topicSubscriptions());
            // 设备上线通知
            notifyDeviceOnline(clientIdentifier);
        });

        // 处理发布消息 (上行)
        endpoint.publishHandler(msg -> {
            String topic = msg.topicName();
            byte[] payload = msg.payload().getBytes();
            handleInboundMqttMessage(clientIdentifier, topic, payload);
            if (msg.qosLevel() == MqttQoS.AT_LEAST_ONCE) {
                endpoint.publishAcknowledge(msg.messageId());
            }
        });

        // 处理断开
        endpoint.disconnectHandler(v -> handleDisconnect(clientIdentifier));
        endpoint.closeHandler(v -> handleDisconnect(clientIdentifier));
    }

    private void handleInboundMqttMessage(String clientId, String topic, byte[] payload) {
        log.debug("[handleInboundMqttMessage][收到来自 {} 的主题 {} 消息, 长度: {}]", clientId, topic, payload.length);
        try {
            UspRecord record = UspProtobufCodec.decodeRecord(payload);
            if (record == null || record.getPayload() == null) {
                return;
            }

            UspMsg msg = UspProtobufCodec.decodeMsg(record.getPayload());
            if (msg == null) {
                return;
            }

            log.info("[handleInboundMqttMessage][USP 消息类型: {}, MsgId: {}, Endpoint: {}]",
                    msg.getMsgType(), msg.getMsgId(), record.getFromId());

            // 1. 若为响应消息，唤醒等待的 RPC Future
            CompletableFuture<UspMsg> future = pendingRpcFutures.remove(msg.getMsgId());
            if (future != null) {
                future.complete(msg);
            }

            // 2. 若包含 GET_RESP，提取物模型并推入总线
            if (msg.getMsgType() == UspMsgType.GET_RESP && msg.getGetResponse() != null) {
                Map<String, Object> props = Tr181ThingModelMapper.mapUspParamsToThingModel(msg.getGetResponse().getEntries());
                if (!props.isEmpty()) {
                    IotMessageBus messageBus = SpringUtil.getBean(IotMessageBus.class);
                    IotDeviceMessage propMsg = IotDeviceMessage.builder()
                            .id(IotDeviceMessageUtils.generateMessageId())
                            .reportTime(LocalDateTime.now())
                            .serverId(getServerId())
                            .method(IotDeviceMessageMethodEnum.PROPERTY_POST.getMethod())
                            .params(props)
                            .build();
                    messageBus.post(IotDeviceMessage.MESSAGE_BUS_DEVICE_MESSAGE_TOPIC, propMsg);
                }
            }
        } catch (Exception e) {
            log.error("[handleInboundMqttMessage][解析 USP 报文异常]", e);
        }
    }

    private void notifyDeviceOnline(String endpointId) {
        IotMessageBus messageBus = SpringUtil.getBean(IotMessageBus.class);
        IotDeviceMessage stateMsg = IotDeviceMessage.builder()
                .id(IotDeviceMessageUtils.generateMessageId())
                .reportTime(LocalDateTime.now())
                .serverId(getServerId())
                .method(IotDeviceMessageMethodEnum.STATE_UPDATE.getMethod())
                .params(new IotDeviceStateUpdateReqDTO(IotDeviceStateEnum.ONLINE.getState()))
                .build();
        messageBus.post(IotDeviceMessage.MESSAGE_BUS_DEVICE_MESSAGE_TOPIC, stateMsg);

        // 主动发送基础 Device.DeviceInfo. 查询
        try {
            UspMsg getMsg = UspMessageBuilder.buildGetMsg("Device.DeviceInfo.");
            publishToAgent(endpointId, UspMessageBuilder.buildRecordBytes(getMsg, endpointId, config.getControllerEndpointId()));
        } catch (Exception e) {
            log.warn("[notifyDeviceOnline][发送初始 DeviceInfo 查询失败: {}]", e.getMessage());
        }
    }

    private void handleDisconnect(String clientId) {
        log.info("[handleDisconnect][USP Agent 断开连接: {}]", clientId);
        clientEndpoints.remove(clientId);
        IotMessageBus messageBus = SpringUtil.getBean(IotMessageBus.class);
        IotDeviceMessage stateMsg = IotDeviceMessage.builder()
                .id(IotDeviceMessageUtils.generateMessageId())
                .reportTime(LocalDateTime.now())
                .serverId(getServerId())
                .method(IotDeviceMessageMethodEnum.STATE_UPDATE.getMethod())
                .params(new IotDeviceStateUpdateReqDTO(IotDeviceStateEnum.OFFLINE.getState()))
                .build();
        messageBus.post(IotDeviceMessage.MESSAGE_BUS_DEVICE_MESSAGE_TOPIC, stateMsg);
    }

    /**
     * 发送下行 USP 报文至 Agent
     */
    public void publishToAgent(String endpointId, byte[] recordBytes) {
        MqttEndpoint endpoint = clientEndpoints.get(endpointId);
        String topic = config.getTopicPrefix() + "/agent/" + endpointId;
        if (endpoint != null && endpoint.isConnected()) {
            endpoint.publish(topic, Buffer.buffer(recordBytes), MqttQoS.AT_LEAST_ONCE, false, false);
            log.info("[publishToAgent][成功发送 USP 报文至 Agent, Topic: {}, 字节数: {}]", topic, recordBytes.length);
        } else {
            log.warn("[publishToAgent][USP Agent {} 未在线或连接已断开]", endpointId);
        }
    }

    /**
     * 同步发送 USP RPC 并等待返回
     */
    public CompletableFuture<UspMsg> executeRpc(String endpointId, UspMsg msg) {
        CompletableFuture<UspMsg> future = new CompletableFuture<>();
        pendingRpcFutures.put(msg.getMsgId(), future);

        try {
            byte[] recordBytes = UspMessageBuilder.buildRecordBytes(msg, endpointId, config.getControllerEndpointId());
            publishToAgent(endpointId, recordBytes);

            long timeout = config.getRequestTimeoutMs() != null ? config.getRequestTimeoutMs() : 30000L;
            CompletableFuture.delayedExecutor(timeout, TimeUnit.MILLISECONDS).execute(() -> {
                if (!future.isDone()) {
                    pendingRpcFutures.remove(msg.getMsgId());
                    future.completeExceptionally(new TimeoutException("USP RPC timeout for " + endpointId));
                }
            });
        } catch (Exception e) {
            pendingRpcFutures.remove(msg.getMsgId());
            future.completeExceptionally(e);
        }
        return future;
    }

    @Override
    public void stop() {
        if (!running) return;
        if (downstreamSubscriber != null) downstreamSubscriber.stop();
        if (mqttServer != null) mqttServer.close();
        if (vertx != null) vertx.close();
        running = false;
        log.info("[stop][TR-369 USP MQTT 协议已停止]");
    }

}
