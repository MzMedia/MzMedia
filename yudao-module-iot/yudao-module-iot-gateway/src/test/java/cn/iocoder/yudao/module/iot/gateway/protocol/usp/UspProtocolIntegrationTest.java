package cn.iocoder.yudao.module.iot.gateway.protocol.usp;

import cn.iocoder.yudao.module.iot.core.protocol.mapper.Tr181ThingModelMapper;
import cn.iocoder.yudao.module.iot.core.protocol.usp.builder.UspMessageBuilder;
import cn.iocoder.yudao.module.iot.core.protocol.usp.codec.UspProtobufCodec;
import cn.iocoder.yudao.module.iot.core.protocol.usp.model.*;
import cn.iocoder.yudao.module.iot.gateway.protocol.usp.client.UspAgentSimulator;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.mqtt.MqttEndpoint;
import io.vertx.mqtt.MqttServer;
import io.vertx.mqtt.MqttServerOptions;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TR-369 USP 协议端到端集成测试
 *
 * 验证：
 * 1. USP Agent 模拟器与 Vert.x MQTT MTP 服务的连接与主题订阅
 * 2. USP GET 请求下发与 Agent 的 GetResponse 回复解析
 * 3. USP SET 请求下发与 Agent 参数树修改及 SetResponse 响应
 * 4. USP OPERATE 命令执行 (Device.Reboot(), Device.IP.Diagnostics.IPPing()) 与 OperateResponse 返回
 * 5. USP NOTIFY 消息由 Agent 主动上报给 Controller
 * 6. TR-181 物模型属性提取映射
 *
 * @author 芋道源码
 */
@Slf4j
public class UspProtocolIntegrationTest {

    private static Vertx vertx;
    private static MqttServer mqttServer;
    private static final int MQTT_PORT = 18835;

    private static final String CONTROLLER_ID = "proto::mzmedia-controller";
    private static final String TOPIC_PREFIX = "usp";

    private static final Map<String, MqttEndpoint> clientEndpoints = new ConcurrentHashMap<>();
    private static final Map<String, CompletableFuture<UspMsg>> pendingResponses = new ConcurrentHashMap<>();
    private static final List<UspMsg> receivedNotifies = new CopyOnWriteArrayList<>();

    @BeforeAll
    public static void setUp() throws Exception {
        vertx = Vertx.vertx();
        MqttServerOptions options = new MqttServerOptions().setPort(MQTT_PORT);
        mqttServer = MqttServer.create(vertx, options);

        mqttServer.endpointHandler(endpoint -> {
            String clientId = endpoint.clientIdentifier();
            log.info("[MockUspServer][Agent 客户端已连接: {}]", clientId);
            clientEndpoints.put(clientId, endpoint);
            endpoint.accept(false);

            endpoint.subscribeHandler(sub -> {
                endpoint.subscribeAcknowledge(sub.messageId(), Collections.singletonList(MqttQoS.AT_LEAST_ONCE));
                log.info("[MockUspServer][Agent 订阅成功: {}, Topics: {}]", clientId, sub.topicSubscriptions());
            });

            endpoint.publishHandler(msg -> {
                byte[] payload = msg.payload().getBytes();
                try {
                    UspRecord record = UspProtobufCodec.decodeRecord(payload);
                    if (record != null && record.getPayload() != null) {
                        UspMsg uspMsg = UspProtobufCodec.decodeMsg(record.getPayload());
                        if (uspMsg != null) {
                            log.info("[MockUspServer][收到 Agent 消息: MsgId={}, 类型={}]",
                                    uspMsg.getMsgId(), uspMsg.getMsgType());

                            // 若为响应，唤醒等待的 Future
                            CompletableFuture<UspMsg> future = pendingResponses.remove(uspMsg.getMsgId());
                            if (future != null) {
                                future.complete(uspMsg);
                            }

                            // 若为 Notify 消息，记录
                            if (uspMsg.getMsgType() == UspMsgType.NOTIFY) {
                                receivedNotifies.add(uspMsg);
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error("[MockUspServer][解析 Agent 报文异常]", e);
                }

                if (msg.qosLevel() == MqttQoS.AT_LEAST_ONCE) {
                    endpoint.publishAcknowledge(msg.messageId());
                }
            });

            endpoint.disconnectHandler(v -> clientEndpoints.remove(clientId));
            endpoint.closeHandler(v -> clientEndpoints.remove(clientId));
        });

        CountDownLatch latch = new CountDownLatch(1);
        mqttServer.listen().onComplete(ar -> {
            if (ar.succeeded()) {
                log.info("[setUp][Mock USP MQTT 服务启动成功，端口: {}]", MQTT_PORT);
                latch.countDown();
            } else {
                log.error("[setUp][Mock USP MQTT 启动失败]", ar.cause());
            }
        });
        assertTrue(latch.await(5, TimeUnit.SECONDS), "MQTT 服务启动超时");
    }

    @AfterAll
    public static void tearDown() {
        if (mqttServer != null) {
            mqttServer.close();
        }
        if (vertx != null) {
            vertx.close();
        }
    }

    /**
     * 发送 USP 请求给 Agent 并等待响应
     */
    private UspMsg sendUspRequestToAgent(String agentEndpointId, UspMsg reqMsg) throws Exception {
        CompletableFuture<UspMsg> future = new CompletableFuture<>();
        pendingResponses.put(reqMsg.getMsgId(), future);

        MqttEndpoint endpoint = clientEndpoints.get(agentEndpointId);
        assertNotNull(endpoint, "Agent 应已在线并被服务端保存");

        byte[] recordBytes = UspMessageBuilder.buildRecordBytes(reqMsg, agentEndpointId, CONTROLLER_ID);
        String topic = TOPIC_PREFIX + "/agent/" + agentEndpointId;

        endpoint.publish(topic, Buffer.buffer(recordBytes), MqttQoS.AT_LEAST_ONCE, false, false);
        return future.get(5, TimeUnit.SECONDS);
    }

    /**
     * 测试场景 1：Agent 连接、订阅以及 Controller 发起 USP GET 查询
     */
    @Test
    public void testUspGetAndThingModelMapping() throws Exception {
        String agentId = "proto::agent-USP_TEST_001";
        UspAgentSimulator agent = new UspAgentSimulator("127.0.0.1", MQTT_PORT, agentId);

        boolean connected = agent.connectSync(3000);
        assertTrue(connected, "Agent 应成功连接到 MQTT 服务端");

        try {
            // Controller 下发 USP GET: 查询 Device.DeviceInfo.
            UspMsg getReq = UspMessageBuilder.buildGetMsg("Device.DeviceInfo.");
            UspMsg resp = sendUspRequestToAgent(agentId, getReq);

            assertNotNull(resp);
            assertEquals(UspMsgType.GET_RESP, resp.getMsgType());
            assertEquals(getReq.getMsgId(), resp.getMsgId());
            assertNotNull(resp.getGetResponse());

            Map<String, String> entries = resp.getGetResponse().getEntries();
            assertFalse(entries.isEmpty());
            assertEquals("Huawei", entries.get("Device.DeviceInfo.Manufacturer"));
            assertEquals("OptiXstar-K662d", entries.get("Device.DeviceInfo.ProductClass"));
            assertEquals("V500R020C10", entries.get("Device.DeviceInfo.SoftwareVersion"));

            // 验证物模型映射
            Map<String, Object> props = Tr181ThingModelMapper.mapUspParamsToThingModel(entries);
            assertEquals("Huawei", props.get("manufacturer"));
            assertEquals("V500R020C10", props.get("firmwareVersion"));

            assertEquals(1, agent.getGetReqCount().get());

        } finally {
            agent.disconnect();
        }
    }

    /**
     * 测试场景 2：USP SET 更新参数测试
     */
    @Test
    public void testUspSet() throws Exception {
        String agentId = "proto::agent-USP_TEST_002";
        UspAgentSimulator agent = new UspAgentSimulator("127.0.0.1", MQTT_PORT, agentId);
        assertTrue(agent.connectSync(3000));

        try {
            // Controller 下发 USP SET: 修改 SSID
            Map<String, String> updateParams = new HashMap<>();
            updateParams.put("Device.WiFi.SSID.1.SSID", "Mesh_WiFi_6G");
            updateParams.put("Device.WiFi.SSID.1.Enable", "false");

            UspMsg setReq = UspMessageBuilder.buildSetMsg(updateParams);
            UspMsg resp = sendUspRequestToAgent(agentId, setReq);

            assertNotNull(resp);
            assertEquals(UspMsgType.SET_RESP, resp.getMsgType());
            assertEquals(setReq.getMsgId(), resp.getMsgId());
            assertTrue(resp.getSetResponse().getSuccess());

            // 验证 Agent 本地参数已更新
            assertEquals("Mesh_WiFi_6G", agent.getParameter("Device.WiFi.SSID.1.SSID"));
            assertEquals("false", agent.getParameter("Device.WiFi.SSID.1.Enable"));
            assertEquals(1, agent.getSetReqCount().get());

        } finally {
            agent.disconnect();
        }
    }

    /**
     * 测试场景 3：USP OPERATE 远程操作测试 (Reboot 与 IPPing)
     */
    @Test
    public void testUspOperate() throws Exception {
        String agentId = "proto::agent-USP_TEST_003";
        UspAgentSimulator agent = new UspAgentSimulator("127.0.0.1", MQTT_PORT, agentId);
        assertTrue(agent.connectSync(3000));

        try {
            // 1. 测试 Reboot 指令
            UspMsg rebootReq = UspMessageBuilder.buildOperateMsg("Device.Reboot()", "CMD_REBOOT_USP_1", Collections.emptyMap());
            UspMsg rebootResp = sendUspRequestToAgent(agentId, rebootReq);
            assertNotNull(rebootResp);
            assertEquals(UspMsgType.OPERATE_RESP, rebootResp.getMsgType());
            assertTrue(rebootResp.getOperateResponse().getExecuted());
            assertEquals(1, agent.getRebootCount().get());

            // 2. 测试 IPPing 诊断指令
            Map<String, String> pingArgs = new HashMap<>();
            pingArgs.put("Host", "8.8.8.8");
            pingArgs.put("NumberOfRepetitions", "4");
            UspMsg pingReq = UspMessageBuilder.buildOperateMsg("Device.IP.Diagnostics.IPPing()", "CMD_PING_1", pingArgs);
            UspMsg pingResp = sendUspRequestToAgent(agentId, pingReq);

            assertNotNull(pingResp);
            assertEquals(UspMsgType.OPERATE_RESP, pingResp.getMsgType());
            assertTrue(pingResp.getOperateResponse().getExecuted());
            assertEquals(1, agent.getPingCount().get());
            Map<String, String> outArgs = pingResp.getOperateResponse().getOutputArgs();
            assertEquals("4", outArgs.get("SuccessCount"));
            assertEquals("0", outArgs.get("FailureCount"));

        } finally {
            agent.disconnect();
        }
    }

    /**
     * 测试场景 4：USP NOTIFY 主动上报通知测试
     */
    @Test
    public void testUspNotify() throws Exception {
        String agentId = "proto::agent-USP_TEST_004";
        UspAgentSimulator agent = new UspAgentSimulator("127.0.0.1", MQTT_PORT, agentId);
        assertTrue(agent.connectSync(3000));

        try {
            Map<String, String> eventParams = new HashMap<>();
            eventParams.put("BootReason", "PowerOn");
            eventParams.put("FirmwareUpdated", "true");

            boolean ok = agent.sendNotify("Device.Boot!", eventParams);
            assertTrue(ok, "Notify 发送应成功");

            // 等待服务端收到 Notify
            Thread.sleep(600);
            assertFalse(receivedNotifies.isEmpty(), "服务端应接收到 Agent 发送的 Notify");
            UspMsg lastNotify = receivedNotifies.get(receivedNotifies.size() - 1);
            assertEquals(UspMsgType.NOTIFY, lastNotify.getMsgType());
            assertNotNull(lastNotify.getNotify());
            assertEquals("Device.Boot!", lastNotify.getNotify().getEventName());
            assertEquals("PowerOn", lastNotify.getNotify().getParams().get("BootReason"));

        } finally {
            agent.disconnect();
        }
    }

}
