package cn.iocoder.yudao.module.iot.gateway.protocol.usp.client;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.module.iot.core.protocol.usp.builder.UspMessageBuilder;
import cn.iocoder.yudao.module.iot.core.protocol.usp.codec.UspProtobufCodec;
import cn.iocoder.yudao.module.iot.core.protocol.usp.model.*;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.mqtt.MqttClient;
import io.vertx.mqtt.MqttClientOptions;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TR-369 USP Agent 模拟客户端 (USP Agent Simulator over MQTT MTP)
 *
 * 模拟遵循 TR-369 规范的 Agent 设备：
 * 1. 维护 TR-181 本地数据模型树快照
 * 2. 通过 MQTT MTP 协议连接服务端 (Vert.x MQTT :18833)
 * 3. 订阅 agent/{agentEndpointId} 主题，接收二进制 Protobuf UspRecord
 * 4. 响应并处理 USP GET、USP SET、USP OPERATE (Reboot, FactoryReset, Download, IPPing 等)
 * 5. 将处理响应封装为 UspRecord 回发至 controller/{controllerEndpointId}
 * 6. 支持主动上报 USP NOTIFY 消息通知
 * 7. 提供独立 main 入口，支持常驻运行或单元测试集成验证
 *
 * @author 芋道源码
 */
@Slf4j
public class UspAgentSimulator {

    @Getter
    @Setter
    private String host = "127.0.0.1";

    @Getter
    @Setter
    private int port = 18833;

    @Getter
    @Setter
    private String agentEndpointId = "proto::agent-USP_SIM_001";

    @Getter
    @Setter
    private String controllerEndpointId = "proto::mzmedia-controller";

    @Getter
    @Setter
    private String topicPrefix = "usp";

    /**
     * TR-181 本地参数树
     */
    private final Map<String, String> parameterTree = new ConcurrentHashMap<>();

    private Vertx vertx;
    private MqttClient mqttClient;

    @Getter
    private volatile boolean connected = false;

    /**
     * 运行状态统计（供测试断言与状态观察）
     */
    @Getter
    private final AtomicInteger getReqCount = new AtomicInteger(0);
    @Getter
    private final AtomicInteger setReqCount = new AtomicInteger(0);
    @Getter
    private final AtomicInteger operateReqCount = new AtomicInteger(0);
    @Getter
    private final AtomicInteger rebootCount = new AtomicInteger(0);
    @Getter
    private final AtomicInteger factoryResetCount = new AtomicInteger(0);
    @Getter
    private final AtomicInteger downloadCount = new AtomicInteger(0);
    @Getter
    private final AtomicInteger pingCount = new AtomicInteger(0);

    @Getter
    private volatile String lastReceivedOperate = "";
    @Getter
    private volatile Map<String, String> lastOperateInputArgs;

    public UspAgentSimulator() {
        initDefaultParameters();
    }

    public UspAgentSimulator(String host, int port, String agentEndpointId) {
        this.host = host;
        this.port = port;
        this.agentEndpointId = agentEndpointId;
        initDefaultParameters();
    }

    /**
     * 初始化默认 TR-181 参数树
     */
    public void initDefaultParameters() {
        parameterTree.clear();
        parameterTree.put("Device.DeviceInfo.Manufacturer", "Huawei");
        parameterTree.put("Device.DeviceInfo.ManufacturerOUI", "00E0FC");
        parameterTree.put("Device.DeviceInfo.ProductClass", "OptiXstar-K662d");
        parameterTree.put("Device.DeviceInfo.SerialNumber", agentEndpointId.replace("proto::agent-", ""));
        parameterTree.put("Device.DeviceInfo.HardwareVersion", "VER.C");
        parameterTree.put("Device.DeviceInfo.SoftwareVersion", "V500R020C10");
        parameterTree.put("Device.DeviceInfo.UpTime", "65432");
        parameterTree.put("Device.WiFi.SSID.1.SSID", "Sim_USP_WiFi_5G");
        parameterTree.put("Device.WiFi.SSID.1.Enable", "true");
        parameterTree.put("Device.IP.Interface.1.IPv4Address.1.IPAddress", "192.168.100.1");
        parameterTree.put("Device.IP.Diagnostics.IPPing.DiagnosticsState", "None");
        parameterTree.put("Device.IP.Diagnostics.IPPing.Host", "8.8.8.8");
    }

    /**
     * 连接到 USP MQTT 服务端并完成订阅
     */
    public CompletableFuture<Boolean> connect() {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        if (connected) {
            future.complete(true);
            return future;
        }

        this.vertx = Vertx.vertx();
        MqttClientOptions options = new MqttClientOptions();
        options.setClientId(agentEndpointId);
        options.setAutoKeepAlive(true);
        options.setKeepAliveInterval(30);

        this.mqttClient = MqttClient.create(vertx, options);

        // 设置消息接收回调
        mqttClient.publishHandler(s -> {
            String topic = s.topicName();
            byte[] payload = s.payload().getBytes();
            handleInboundMessage(topic, payload);
        });

        mqttClient.connect(port, host).onComplete(s -> {
            if (s.succeeded()) {
                connected = true;
                log.info("[connect][USP Agent 成功连接到 MQTT 服务端: {}:{}]", host, port);

                // 订阅 Agent 专属主题: usp/agent/{agentEndpointId}
                String subTopic = topicPrefix + "/agent/" + agentEndpointId;
                mqttClient.subscribe(subTopic, MqttQoS.AT_LEAST_ONCE.value()).onComplete(subAck -> {
                    if (subAck.succeeded()) {
                        log.info("[connect][USP Agent 成功订阅下行主题: {}]", subTopic);
                        future.complete(true);
                    } else {
                        log.error("[connect][USP Agent 订阅失败: {}]", subTopic, subAck.cause());
                        future.completeExceptionally(subAck.cause());
                    }
                });
            } else {
                connected = false;
                log.error("[connect][USP Agent 连接失败: {}:{}]", host, port, s.cause());
                future.completeExceptionally(s.cause());
            }
        });

        return future;
    }

    /**
     * 同步等待连接完成
     */
    public boolean connectSync(long timeoutMs) {
        try {
            return connect().get(timeoutMs, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.error("[connectSync][连接超时或异常: {}]", e.getMessage());
            return false;
        }
    }

    /**
     * 断开连接
     */
    public void disconnect() {
        if (!connected) return;
        if (mqttClient != null) {
            mqttClient.disconnect();
            mqttClient = null;
        }
        if (vertx != null) {
            vertx.close();
            vertx = null;
        }
        connected = false;
        log.info("[disconnect][USP Agent 已断开连接]");
    }

    // ==================== 接收并处理 USP 消息 ====================

    private void handleInboundMessage(String topic, byte[] payload) {
        log.debug("[handleInboundMessage][收到下行消息, Topic: {}, 字节数: {}]", topic, payload.length);
        try {
            // 1. 解码 UspRecord
            UspRecord record = UspProtobufCodec.decodeRecord(payload);
            if (record == null || record.getPayload() == null) {
                log.warn("[handleInboundMessage][无效的 UspRecord]");
                return;
            }

            // 2. 解码 UspMsg
            UspMsg reqMsg = UspProtobufCodec.decodeMsg(record.getPayload());
            if (reqMsg == null) {
                log.warn("[handleInboundMessage][无效的 UspMsg]");
                return;
            }

            String controllerId = StrUtil.blankToDefault(record.getFromId(), controllerEndpointId);
            log.info("[handleInboundMessage][处理 USP 请求: 类型={}, MsgId={}, 来源={}]",
                    reqMsg.getMsgType(), reqMsg.getMsgId(), controllerId);

            // 3. 针对不同类型的请求执行操作并回复
            UspMsg respMsg = null;
            if (reqMsg.getMsgType() == UspMsgType.GET && reqMsg.getGetRequest() != null) {
                respMsg = handleGetRequest(reqMsg);
            } else if (reqMsg.getMsgType() == UspMsgType.SET && reqMsg.getSetRequest() != null) {
                respMsg = handleSetRequest(reqMsg);
            } else if (reqMsg.getMsgType() == UspMsgType.OPERATE && reqMsg.getOperateRequest() != null) {
                respMsg = handleOperateRequest(reqMsg);
            }

            // 4. 发送 Response
            if (respMsg != null) {
                sendResponse(controllerId, respMsg);
            }

        } catch (Exception e) {
            log.error("[handleInboundMessage][处理 USP 下行请求异常]", e);
        }
    }

    private UspMsg handleGetRequest(UspMsg reqMsg) {
        getReqCount.incrementAndGet();
        UspGetRequest getReq = reqMsg.getGetRequest();
        Map<String, String> entries = new HashMap<>();

        if (getReq.getParamPaths() != null) {
            for (String p : getReq.getParamPaths()) {
                if (p.endsWith(".")) {
                    // 前缀匹配
                    for (Map.Entry<String, String> entry : parameterTree.entrySet()) {
                        if (entry.getKey().startsWith(p)) {
                            entries.put(entry.getKey(), entry.getValue());
                        }
                    }
                } else {
                    String val = parameterTree.get(p);
                    if (val != null) {
                        entries.put(p, val);
                    }
                }
            }
        }

        log.info("[handleGetRequest][返回 GET 结果, 匹配参数项: {}]", entries.size());
        return UspMsg.builder()
                .msgId(reqMsg.getMsgId())
                .msgType(UspMsgType.GET_RESP)
                .getResponse(UspGetResponse.builder().entries(entries).build())
                .build();
    }

    private UspMsg handleSetRequest(UspMsg reqMsg) {
        setReqCount.incrementAndGet();
        UspSetRequest setReq = reqMsg.getSetRequest();
        Map<String, String> updated = new HashMap<>();

        if (setReq.getParams() != null) {
            for (Map.Entry<String, String> entry : setReq.getParams().entrySet()) {
                parameterTree.put(entry.getKey(), entry.getValue());
                updated.put(entry.getKey(), entry.getValue());
                log.info("[handleSetRequest][SET 更新参数: {} = {}]", entry.getKey(), entry.getValue());
            }
        }

        return UspMsg.builder()
                .msgId(reqMsg.getMsgId())
                .msgType(UspMsgType.SET_RESP)
                .setResponse(UspSetResponse.builder().success(true).updatedParams(updated).build())
                .build();
    }

    private UspMsg handleOperateRequest(UspMsg reqMsg) {
        operateReqCount.incrementAndGet();
        UspOperateRequest opReq = reqMsg.getOperateRequest();
        String cmd = opReq.getCommand();
        this.lastReceivedOperate = cmd;
        this.lastOperateInputArgs = opReq.getInputArgs();
        Map<String, String> outArgs = new HashMap<>();

        log.info("[handleOperateRequest][执行 OPERATE 指令: {}, CommandKey: {}]", cmd, opReq.getCommandKey());

        if ("Device.Reboot()".equalsIgnoreCase(cmd)) {
            rebootCount.incrementAndGet();
            outArgs.put("Status", "Rebooting");
        } else if ("Device.FactoryReset()".equalsIgnoreCase(cmd)) {
            factoryResetCount.incrementAndGet();
            initDefaultParameters();
            outArgs.put("Status", "Resetting");
        } else if ("Device.Download()".equalsIgnoreCase(cmd)) {
            downloadCount.incrementAndGet();
            outArgs.put("Status", "DownloadStarted");
        } else if ("Device.IP.Diagnostics.IPPing()".equalsIgnoreCase(cmd)) {
            pingCount.incrementAndGet();
            outArgs.put("SuccessCount", "4");
            outArgs.put("FailureCount", "0");
            outArgs.put("AverageResponseTime", "15");
            outArgs.put("MinimumResponseTime", "12");
            outArgs.put("MaximumResponseTime", "18");
        } else {
            outArgs.put("Status", "Executed");
        }

        return UspMsg.builder()
                .msgId(reqMsg.getMsgId())
                .msgType(UspMsgType.OPERATE_RESP)
                .operateResponse(UspOperateResponse.builder().executed(true).outputArgs(outArgs).build())
                .build();
    }

    /**
     * 发送响应报文给 Controller
     */
    private void sendResponse(String toControllerId, UspMsg respMsg) throws IOException {
        byte[] recordBytes = UspMessageBuilder.buildRecordBytes(respMsg, toControllerId, agentEndpointId);
        String pubTopic = topicPrefix + "/controller/" + toControllerId;

        if (mqttClient != null && connected) {
            mqttClient.publish(pubTopic, Buffer.buffer(recordBytes), MqttQoS.AT_LEAST_ONCE, false, false);
            log.info("[sendResponse][已将 {} 响应发送至 Controller, Topic: {}, 字节数: {}]",
                    respMsg.getMsgType(), pubTopic, recordBytes.length);
        }
    }

    // ==================== 主动上报 Notify ====================

    /**
     * 主动发送 USP Notify 通知
     */
    public boolean sendNotify(String eventName, Map<String, String> params) {
        if (!connected) return false;
        try {
            UspMsg notifyMsg = UspMsg.builder()
                    .msgId(IdUtil.fastSimpleUUID())
                    .msgType(UspMsgType.NOTIFY)
                    .notify(UspNotify.builder()
                            .subscriptionId("sub_001")
                            .sendResp(false)
                            .type("Event")
                            .eventName(eventName)
                            .params(params)
                            .build())
                    .build();

            sendResponse(controllerEndpointId, notifyMsg);
            log.info("[sendNotify][成功发送 USP NOTIFY 事件: {}]", eventName);
            return true;
        } catch (Exception e) {
            log.error("[sendNotify][发送 USP NOTIFY 失败]", e);
            return false;
        }
    }

    // ==================== 参数读写便捷方法 ====================

    public String getParameter(String name) {
        return parameterTree.get(name);
    }

    public void setParameter(String name, String value) {
        parameterTree.put(name, value);
    }

    // ==================== 独立运行 Main 方法 ====================

    public static void main(String[] args) throws Exception {
        String host = args.length > 0 ? args[0] : "127.0.0.1";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 18833;
        String agentId = args.length > 2 ? args[2] : "proto::agent-USP_SIM_" + System.currentTimeMillis() % 10000;

        UspAgentSimulator agent = new UspAgentSimulator(host, port, agentId);
        System.out.println("==================================================");
        System.out.println("TR-369 USP Agent 模拟客户端启动中...");
        System.out.println("AgentEndpoint: " + agentId);
        System.out.println("MQTT Broker: " + host + ":" + port);
        System.out.println("==================================================");

        boolean ok = agent.connectSync(5000);
        if (!ok) {
            System.err.println("连接 MQTT 服务端失败，请确认服务端已启动！");
            System.exit(1);
        }

        System.out.println("Agent 现已在线，正在监听 Controller RPC 指令...");
        Runtime.getRuntime().addShutdownHook(new Thread(agent::disconnect));
        Thread.currentThread().join();
    }

}
