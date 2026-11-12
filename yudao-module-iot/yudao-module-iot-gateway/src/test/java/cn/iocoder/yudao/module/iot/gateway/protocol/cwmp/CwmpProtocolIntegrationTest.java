package cn.iocoder.yudao.module.iot.gateway.protocol.cwmp;

import cn.hutool.core.util.IdUtil;
import cn.iocoder.yudao.module.iot.core.protocol.cwmp.builder.CwmpSoapBuilder;
import cn.iocoder.yudao.module.iot.core.protocol.cwmp.model.*;
import cn.iocoder.yudao.module.iot.core.protocol.cwmp.parser.CwmpSoapParser;
import cn.iocoder.yudao.module.iot.core.protocol.mapper.Tr181ThingModelMapper;
import cn.iocoder.yudao.module.iot.gateway.protocol.cwmp.client.CwmpCpeSimulator;
import cn.iocoder.yudao.module.iot.gateway.protocol.cwmp.session.CwmpSession;
import cn.iocoder.yudao.module.iot.gateway.protocol.cwmp.session.IotCwmpSessionManager;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TR-069 CWMP 协议端到端集成测试
 *
 * 验证：
 * 1. CPE 模拟器与 Vert.x ACS 服务端的 HTTP SOAP 会话交互（Inform -> InformResponse -> 空 POST 出队 -> RPC 交互 -> 204 会话结束）
 * 2. ACS RPC 下发（GetParameterValues / SetParameterValues / Reboot / Download）与 CPE 的响应执行
 * 3. Connection Request 机制：ACS 通过 HTTP Digest 鉴权发起唤醒 GET 请求，CPE 验证通过并自动触发 Inform
 * 4. TR-181 / TR-098 物模型参数映射
 *
 * @author 芋道源码
 */
@Slf4j
public class CwmpProtocolIntegrationTest {

    private static Vertx vertx;
    private static HttpServer acsServer;
    private static final int ACS_PORT = 19292;
    private static final String ACS_URL = "http://127.0.0.1:" + ACS_PORT + "/cwmp";

    private static final int CPE_CR_PORT = 17547;
    private static IotCwmpSessionManager sessionManager;
    private static final Map<String, List<String>> queuedCommands = new HashMap<>();

    @BeforeAll
    public static void setUp() throws Exception {
        vertx = Vertx.vertx();
        sessionManager = new IotCwmpSessionManager();

        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());

        // 模拟 ACS 核心服务端路由 (/cwmp)
        router.post("/cwmp").handler(ctx -> {
            String cookie = ctx.request().getHeader("Cookie");
            String reqBody = ctx.body().asString();
            String msgType = CwmpSoapParser.getMessageType(reqBody);

            log.info("[MockACS][收到 CPE 请求, 类型: {}, Cookie: {}]", msgType, cookie);

            if ("Inform".equalsIgnoreCase(msgType)) {
                CwmpInform inform = CwmpSoapParser.parseInform(reqBody);
                String sn = inform != null ? inform.getSerialNumber() : "UNKNOWN";

                CwmpSession session = sessionManager.getOrCreateSession(sn);
                session.setLastInform(inform);

                ctx.response().putHeader("Set-Cookie", "CWMP_SESSION=" + session.getSessionId() + "; Path=/");
                ctx.response().putHeader("Content-Type", "text/xml; charset=utf-8");
                ctx.response().setStatusCode(200);
                ctx.response().end(CwmpSoapBuilder.buildInformResponse(inform.getHeaderId()));
                return;
            }

            // 出队或处理 RPC Response
            CwmpSession session = sessionManager.getSessionByCookie(cookie);
            if (session == null) {
                log.warn("[MockACS][未找到有效会话, Cookie: {}]", cookie);
                ctx.response().setStatusCode(204).end();
                return;
            }

            // 检查是否有待下发的测试 RPC 命令
            List<String> cmdQueue = queuedCommands.get(session.getSerialNumber());
            if (cmdQueue != null && !cmdQueue.isEmpty()) {
                String nextRpc = cmdQueue.remove(0);
                log.info("[MockACS][下发队列指令至 CPE: {}]", CwmpSoapParser.getMessageType(nextRpc));
                ctx.response().putHeader("Content-Type", "text/xml; charset=utf-8");
                ctx.response().setStatusCode(200);
                ctx.response().end(nextRpc);
            } else {
                // 队列为空，返回 204 No Content 结束会话
                log.info("[MockACS][队列为空，回复 204 结束会话]");
                ctx.response().setStatusCode(204).end();
            }
        });

        CountDownLatch latch = new CountDownLatch(1);
        vertx.createHttpServer().requestHandler(router).listen(ACS_PORT).onComplete(res -> {
            if (res.succeeded()) {
                acsServer = res.result();
                log.info("[setUp][Mock ACS HTTP 服务启动成功，端口: {}]", ACS_PORT);
                latch.countDown();
            } else {
                log.error("[setUp][Mock ACS 启动失败]", res.cause());
            }
        });
        assertTrue(latch.await(5, TimeUnit.SECONDS), "ACS 服务端启动超时");
    }

    @AfterAll
    public static void tearDown() {
        if (acsServer != null) {
            acsServer.close();
        }
        if (vertx != null) {
            vertx.close();
        }
    }

    /**
     * 测试场景 1：CPE 主动向 ACS 发送 0 BOOTSTRAP 注册并完成会话
     */
    @Test
    public void testInformAndSessionLifecycle() {
        String sn = "TEST_SN_001";
        CwmpCpeSimulator cpe = new CwmpCpeSimulator(ACS_URL, sn, CPE_CR_PORT);

        // 发送开机注册
        boolean success = cpe.sendBootstrap();
        assertTrue(success, "CPE Inform 会话执行应返回成功");
        assertEquals(1, cpe.getInformCount().get(), "Inform 计数应为 1");
        assertNotNull(cpe.getSessionCookie(), "CPE 应成功保存 ACS 下发的会话 Cookie");

        // 验证 ACS 端的 Session 状态
        CwmpSession session = sessionManager.getSessionByCookie(cpe.getSessionCookie());
        assertNotNull(session, "ACS 应存在该会话对象");
        assertEquals(sn, session.getSerialNumber());
        assertNotNull(session.getLastInform());
        assertEquals("ADB Broadband", session.getLastInform().getManufacturer());
        assertTrue(session.getLastInform().hasBootstrapEvent());

        // 验证物模型参数映射
        Map<String, Object> props = Tr181ThingModelMapper.mapCwmpParamsToThingModel(session.getLastInform().getParameterList());
        assertEquals("ADB Broadband", props.get("manufacturer"));
        assertEquals("V1.0.0-PROD", props.get("firmwareVersion"));
        assertEquals("192.168.1.100", props.get("wanIp"));
    }

    /**
     * 测试场景 2：ACS 下发 GetParameterValues 与 SetParameterValues 指令
     */
    @Test
    public void testAcsRpcGetAndSetParameterValues() {
        String sn = "TEST_SN_RPC_002";
        CwmpCpeSimulator cpe = new CwmpCpeSimulator(ACS_URL, sn, CPE_CR_PORT + 1);

        // 预设待下发指令队列：
        // 1. SetParameterValues (修改 WiFi 名称为 "My_Fiber_WiFi")
        Map<String, String> setParams = new HashMap<>();
        setParams.put("Device.WiFi.SSID.1.SSID", "My_Fiber_WiFi");
        String spvXml = CwmpSoapBuilder.buildSetParameterValues(setParams);

        // 2. GetParameterValues (读取 Device.WiFi.SSID.1.SSID)
        String gpvXml = CwmpSoapBuilder.buildGetParameterValues(Collections.singletonList("Device.WiFi.SSID.1.SSID"));

        queuedCommands.put(sn, new ArrayList<>(Arrays.asList(spvXml, gpvXml)));

        // CPE 发起 Inform 会话并出队执行指令
        boolean ok = cpe.sendBoot();
        assertTrue(ok, "会话执行成功");

        // 验证 CPE 本地参数是否被修改
        assertEquals("My_Fiber_WiFi", cpe.getParameter("Device.WiFi.SSID.1.SSID"), "参数树中的 SSID 应已被成功更新");
    }

    /**
     * 测试场景 3：ACS 下发 Reboot 与 Download 固件升级指令
     */
    @Test
    public void testAcsRpcRebootAndDownload() {
        String sn = "TEST_SN_RPC_003";
        CwmpCpeSimulator cpe = new CwmpCpeSimulator(ACS_URL, sn, CPE_CR_PORT + 2);

        // 预设下发指令：Reboot 与 Download
        String rebootXml = CwmpSoapBuilder.buildReboot("CMD_REBOOT_001");
        String downloadXml = CwmpSoapBuilder.buildDownload(
                "1 Firmware Upgrade Image",
                "http://firmware.example.com/v2.0.0.bin",
                "fw_user",
                "fw_pass",
                2048000L,
                "v2.0.0.bin"
        );

        queuedCommands.put(sn, new ArrayList<>(Arrays.asList(rebootXml, downloadXml)));

        boolean ok = cpe.sendPeriodic();
        assertTrue(ok, "会话执行成功");

        // 验证 CPE 执行统计
        assertEquals(1, cpe.getRebootCount().get(), "Reboot 次数应为 1");
        assertEquals(1, cpe.getDownloadCount().get(), "Download 次数应为 1");
        assertNotNull(cpe.getLastDownloadRequest());
        assertEquals("http://firmware.example.com/v2.0.0.bin", cpe.getLastDownloadRequest().getUrl());
        assertEquals(2048000L, cpe.getLastDownloadRequest().getFileSize());
    }

    /**
     * 测试场景 4：Connection Request 反向唤醒与 HTTP Digest 鉴权测试
     */
    @Test
    public void testConnectionRequestDigestAuthAndInformTrigger() throws Exception {
        String sn = "TEST_SN_CR_004";
        int crPort = CPE_CR_PORT + 3;
        CwmpCpeSimulator cpe = new CwmpCpeSimulator(ACS_URL, sn, crPort);
        cpe.setCrUsername("acs_admin");
        cpe.setCrPassword("acs_pass123");

        // 启动 CPE 端的 Connection Request HTTP 监听服务
        cpe.start();

        try {
            String crUrl = "http://127.0.0.1:" + crPort + cpe.getConnectionRequestPath();
            IotCwmpConnectionRequestClient crClient = new IotCwmpConnectionRequestClient();

            // 1. 使用错误的凭证测试，应失败 (401)
            boolean failResult = crClient.trigger(crUrl, "wrong_user", "wrong_pass");
            assertFalse(failResult, "错误密码鉴权应当失败");

            // 2. 使用正确的凭证发起唤醒
            boolean successResult = crClient.trigger(crUrl, "acs_admin", "acs_pass123");
            assertTrue(successResult, "正确凭证鉴权应当成功唤醒 CPE");
            assertTrue(cpe.getConnectionRequestReceivedCount().get() >= 2, "CPE 应接收到至少 2 次 HTTP 访问");

            // 3. 等待 CPE 异步回连发起 Inform (6 CONNECTION REQUEST)
            Thread.sleep(800);
            assertTrue(cpe.getInformCount().get() >= 1, "CPE 被唤醒后应自动发起 Inform 会话");

        } finally {
            cpe.stop();
        }
    }

}
