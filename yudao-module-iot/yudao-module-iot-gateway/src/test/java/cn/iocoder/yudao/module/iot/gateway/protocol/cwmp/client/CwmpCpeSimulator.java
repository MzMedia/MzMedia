package cn.iocoder.yudao.module.iot.gateway.protocol.cwmp.client;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.iocoder.yudao.module.iot.core.protocol.cwmp.builder.CwmpSoapBuilder;
import cn.iocoder.yudao.module.iot.core.protocol.cwmp.model.*;
import cn.iocoder.yudao.module.iot.core.protocol.cwmp.parser.CwmpSoapParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TR-069 CPE 模拟客户端 (CWMP Client Simulator)
 *
 * 模拟真实光猫 / 宽带网关设备：
 * 1. 维护 TR-098 / TR-181 本地参数树
 * 2. 主动向 ACS (Vert.x HTTP :9292/cwmp) 发起 Inform (0 BOOTSTRAP, 1 BOOT, 2 PERIODIC, 6 CONNECTION REQUEST)
 * 3. 严格遵循 CWMP 会话生命周期与 Cookie 机制，持续发送空 POST 出队 ACS 指令队列
 * 4. 响应并执行 ACS RPC 指令：GetParameterValues, SetParameterValues, GetParameterNames, Reboot, FactoryReset, Download
 * 5. 内置 HTTP Server 监听 ConnectionRequestURL (默认 :7547)，支持 HTTP Digest 认证，被唤醒后自动回连 ACS
 * 6. 提供独立 main 入口，支持常驻运行或单元测试集成验证
 *
 * @author 芋道源码
 */
@Slf4j
public class CwmpCpeSimulator {

    @Getter
    @Setter
    private String acsUrl = "http://127.0.0.1:9292/cwmp";

    @Getter
    @Setter
    private String manufacturer = "ADB Broadband";

    @Getter
    @Setter
    private String oui = "0013C8";

    @Getter
    @Setter
    private String productClass = "VV5522";

    @Getter
    @Setter
    private String serialNumber = "SN123456789";

    @Getter
    @Setter
    private int connectionRequestPort = 7547;

    @Getter
    @Setter
    private String connectionRequestPath = "/ConnectionRequest-123";

    @Getter
    @Setter
    private String crUsername = "admin";

    @Getter
    @Setter
    private String crPassword = "admin";

    /**
     * 本地 TR-098 / TR-181 参数树快照
     */
    private final Map<String, String> parameterTree = new ConcurrentHashMap<>();

    /**
     * 当前维持的 ACS 会话 Cookie (例如 CWMP_SESSION=xxx)
     */
    @Getter
    private volatile String sessionCookie;

    /**
     * 内置 Connection Request HTTP Server
     */
    private HttpServer crServer;

    private final ExecutorService threadPool = Executors.newCachedThreadPool();

    /**
     * 运行状态统计（供测试断言与状态观察）
     */
    @Getter
    private final AtomicInteger informCount = new AtomicInteger(0);
    @Getter
    private final AtomicInteger rebootCount = new AtomicInteger(0);
    @Getter
    private final AtomicInteger factoryResetCount = new AtomicInteger(0);
    @Getter
    private final AtomicInteger downloadCount = new AtomicInteger(0);
    @Getter
    private final AtomicInteger connectionRequestReceivedCount = new AtomicInteger(0);

    @Getter
    private volatile String lastReceivedRpc = "";
    @Getter
    private volatile CwmpDownloadRequest lastDownloadRequest;

    public CwmpCpeSimulator() {
        initDefaultParameters();
    }

    public CwmpCpeSimulator(String acsUrl, String serialNumber, int crPort) {
        this.acsUrl = acsUrl;
        this.serialNumber = serialNumber;
        this.connectionRequestPort = crPort;
        this.connectionRequestPath = "/ConnectionRequest-" + serialNumber;
        initDefaultParameters();
    }

    /**
     * 初始化默认 TR-098 / TR-181 参数树
     */
    public void initDefaultParameters() {
        parameterTree.clear();
        String crUrl = "http://127.0.0.1:" + connectionRequestPort + connectionRequestPath;

        // TR-098 常用参数
        parameterTree.put("InternetGatewayDevice.DeviceInfo.Manufacturer", manufacturer);
        parameterTree.put("InternetGatewayDevice.DeviceInfo.ManufacturerOUI", oui);
        parameterTree.put("InternetGatewayDevice.DeviceInfo.ProductClass", productClass);
        parameterTree.put("InternetGatewayDevice.DeviceInfo.SerialNumber", serialNumber);
        parameterTree.put("InternetGatewayDevice.DeviceInfo.HardwareVersion", "HW-V1.0");
        parameterTree.put("InternetGatewayDevice.DeviceInfo.SoftwareVersion", "V1.0.0-PROD");
        parameterTree.put("InternetGatewayDevice.DeviceInfo.SpecVersion", "1.0");
        parameterTree.put("InternetGatewayDevice.DeviceInfo.UpTime", "12345");
        parameterTree.put("InternetGatewayDevice.ManagementServer.ConnectionRequestURL", crUrl);
        parameterTree.put("InternetGatewayDevice.ManagementServer.ConnectionRequestUsername", crUsername);
        parameterTree.put("InternetGatewayDevice.ManagementServer.ConnectionRequestPassword", crPassword);
        parameterTree.put("InternetGatewayDevice.ManagementServer.PeriodicInformEnable", "true");
        parameterTree.put("InternetGatewayDevice.ManagementServer.PeriodicInformInterval", "60");
        parameterTree.put("InternetGatewayDevice.ManagementServer.URL", acsUrl);
        parameterTree.put("InternetGatewayDevice.WANDevice.1.WANConnectionDevice.1.WANIPConnection.1.ExternalIPAddress", "192.168.1.100");
        parameterTree.put("InternetGatewayDevice.LANDevice.1.WLANConfiguration.1.SSID", "Sim_WiFi_2.4G");
        parameterTree.put("InternetGatewayDevice.LANDevice.1.WLANConfiguration.1.Enable", "true");

        // TR-181 常用参数对照
        parameterTree.put("Device.DeviceInfo.Manufacturer", manufacturer);
        parameterTree.put("Device.DeviceInfo.ManufacturerOUI", oui);
        parameterTree.put("Device.DeviceInfo.ProductClass", productClass);
        parameterTree.put("Device.DeviceInfo.SerialNumber", serialNumber);
        parameterTree.put("Device.DeviceInfo.HardwareVersion", "HW-V1.0");
        parameterTree.put("Device.DeviceInfo.SoftwareVersion", "V1.0.0-PROD");
        parameterTree.put("Device.ManagementServer.ConnectionRequestURL", crUrl);
        parameterTree.put("Device.ManagementServer.ConnectionRequestUsername", crUsername);
        parameterTree.put("Device.ManagementServer.ConnectionRequestPassword", crPassword);
        parameterTree.put("Device.ManagementServer.PeriodicInformEnable", "true");
        parameterTree.put("Device.ManagementServer.PeriodicInformInterval", "60");
        parameterTree.put("Device.ManagementServer.URL", acsUrl);
        parameterTree.put("Device.WiFi.SSID.1.SSID", "Sim_WiFi_2.4G");
        parameterTree.put("Device.WiFi.SSID.1.Enable", "true");
        parameterTree.put("Device.IP.Interface.1.IPv4Address.1.IPAddress", "192.168.1.100");
    }

    /**
     * 启动 CPE 模拟器，监听 Connection Request HTTP 端口
     */
    public synchronized void start() throws IOException {
        if (crServer != null) {
            return;
        }
        crServer = HttpServer.create(new InetSocketAddress(connectionRequestPort), 0);
        crServer.createContext(connectionRequestPath, new ConnectionRequestHandler());
        crServer.setExecutor(threadPool);
        crServer.start();
        log.info("[CwmpCpeSimulator][已启动 Connection Request 监听: http://127.0.0.1:{}{}]",
                connectionRequestPort, connectionRequestPath);
    }

    /**
     * 停止 CPE 模拟器服务
     */
    public synchronized void stop() {
        if (crServer != null) {
            crServer.stop(0);
            crServer = null;
            log.info("[CwmpCpeSimulator][已停止 Connection Request 监听服务]");
        }
        threadPool.shutdownNow();
    }

    // ==================== 主动 Inform 触发方法 ====================

    /**
     * 发送 BOOTSTRAP (首次上线注册，事件代码 0 BOOTSTRAP)
     */
    public boolean sendBootstrap() {
        List<CwmpEvent> events = Arrays.asList(
                new CwmpEvent("0 BOOTSTRAP", ""),
                new CwmpEvent("1 BOOT", "")
        );
        return executeSession(events);
    }

    /**
     * 发送 BOOT (开机重启，事件代码 1 BOOT)
     */
    public boolean sendBoot() {
        List<CwmpEvent> events = Collections.singletonList(new CwmpEvent("1 BOOT", ""));
        return executeSession(events);
    }

    /**
     * 发送 PERIODIC (周期性心跳 Inform，事件代码 2 PERIODIC)
     */
    public boolean sendPeriodic() {
        List<CwmpEvent> events = Collections.singletonList(new CwmpEvent("2 PERIODIC", ""));
        return executeSession(events);
    }

    /**
     * 发送 CONNECTION REQUEST (被 ACS 唤醒后发起，事件代码 6 CONNECTION REQUEST)
     */
    public boolean sendConnectionRequest() {
        List<CwmpEvent> events = Collections.singletonList(new CwmpEvent("6 CONNECTION REQUEST", ""));
        return executeSession(events);
    }

    /**
     * 发送 VALUE CHANGE (参数变更上报，事件代码 4 VALUE CHANGE)
     */
    public boolean sendValueChange(String paramName, String newValue) {
        parameterTree.put(paramName, newValue);
        List<CwmpEvent> events = Collections.singletonList(new CwmpEvent("4 VALUE CHANGE", ""));
        return executeSession(events);
    }

    /**
     * 发送 TRANSFER COMPLETE (固件下载升级完成通知，事件代码 7 TRANSFER COMPLETE)
     */
    public boolean sendTransferComplete(String commandKey, int faultCode, String faultString) {
        List<CwmpEvent> events = Collections.singletonList(new CwmpEvent("7 TRANSFER COMPLETE", commandKey));
        return executeSession(events, (sessionId) -> CwmpSoapBuilder.buildTransferComplete(sessionId, commandKey, faultCode, faultString));
    }

    // ==================== 核心 CWMP 事务会话循环 ====================

    public boolean executeSession(List<CwmpEvent> events) {
        return executeSession(events, null);
    }

    /**
     * 执行完整的 CWMP 会话生命周期
     *
     * 1. CPE -> ACS: POST Inform
     * 2. ACS -> CPE: 200 InformResponse (携带 Set-Cookie)
     * 3. 循环出队 (While true):
     *      CPE -> ACS: POST (携带 Cookie, Body 为上一个 RPC Response 或空 Body)
     *      ACS -> CPE: 200 RPC Request (如 GetParameterValues) / 204 No Content (出队结束)
     *
     * @param events 本次 Inform 携带的事件列表
     * @param initialActionHook 会话 Inform 之后可选的主动报文生成器 (例如 TransferComplete)
     * @return 会话是否正常完成
     */
    public synchronized boolean executeSession(List<CwmpEvent> events, java.util.function.Function<String, String> initialActionHook) {
        log.info("[executeSession][CPE 开始向 ACS 发起 Inform 会话, 事件: {}]", events);
        try {
            // 1. 构建 Inform SOAP 报文
            String headerId = String.valueOf(System.currentTimeMillis() % 100000);
            CwmpDeviceId deviceId = CwmpDeviceId.builder()
                    .manufacturer(manufacturer)
                    .oui(oui)
                    .productClass(productClass)
                    .serialNumber(serialNumber)
                    .build();

            List<CwmpParameterValue> paramList = new ArrayList<>();
            for (Map.Entry<String, String> entry : parameterTree.entrySet()) {
                paramList.add(new CwmpParameterValue(entry.getKey(), entry.getValue(), "xsd:string"));
            }

            CwmpInform inform = CwmpInform.builder()
                    .headerId(headerId)
                    .deviceId(deviceId)
                    .events(events)
                    .parameterList(paramList)
                    .retryCount(0)
                    .build();
            String informXml = CwmpSoapBuilder.buildInform(inform);

            // 2. 发送 Inform POST
            HttpResponse informResp = sendHttpPost(acsUrl, informXml, sessionCookie);
            if (informResp.code != 200) {
                log.error("[executeSession][Inform 失败, HTTP 状态码: {}]", informResp.code);
                return false;
            }

            // 保存/刷新 Session Cookie
            if (StrUtil.isNotBlank(informResp.cookie)) {
                this.sessionCookie = informResp.cookie;
            }

            String respType = CwmpSoapParser.getMessageType(informResp.body);
            if (!"InformResponse".equalsIgnoreCase(respType)) {
                log.error("[executeSession][未收到 InformResponse, 收到: {}]", respType);
                return false;
            }
            informCount.incrementAndGet();
            log.info("[executeSession][收到 InformResponse, 开始进入空 POST 出队循环...]");

            // 3. 循环发送 POST 获取并处理 ACS 的 RPC 请求
            String nextBody = initialActionHook != null ? initialActionHook.apply(headerId) : "";

            while (true) {
                HttpResponse rpcResp = sendHttpPost(acsUrl, nextBody, sessionCookie);

                // 若收到 204 No Content 或空 Body，说明 ACS 下发队列已空，会话正常结束！
                if (rpcResp.code == 204 || StrUtil.isBlank(rpcResp.body)) {
                    log.info("[executeSession][ACS 返回 204 No Content 或空 Body，会话正常结束]");
                    break;
                }

                if (rpcResp.code != 200) {
                    log.error("[executeSession][收到非 200/204 异常状态码: {}]", rpcResp.code);
                    break;
                }

                // 解析 ACS 下发的 RPC 指令
                String rpcType = CwmpSoapParser.getMessageType(rpcResp.body);
                String rpcHeaderId = StrUtil.blankToDefault(CwmpSoapParser.getHeaderId(rpcResp.body), IdUtil.simpleUUID().substring(0, 8));
                this.lastReceivedRpc = rpcType;
                log.info("[executeSession][收到 ACS 下发 RPC 请求: {}, ID: {}]", rpcType, rpcHeaderId);

                // 根据指令类型执行并生成下一轮 POST Body
                nextBody = handleAcsRpc(rpcType, rpcHeaderId, rpcResp.body);
            }

            return true;
        } catch (Exception e) {
            log.error("[executeSession][CWMP 会话执行异常]", e);
            return false;
        }
    }

    /**
     * 处理 ACS 下发的 RPC 指令，生成回复报文
     */
    private String handleAcsRpc(String rpcType, String headerId, String xml) {
        if ("GetParameterValues".equalsIgnoreCase(rpcType)) {
            List<String> queryNames = CwmpSoapParser.parseGetParameterValues(xml);
            List<CwmpParameterValue> resultValues = new ArrayList<>();
            for (String qn : queryNames) {
                if (qn.endsWith(".")) {
                    // 前缀匹配（对象节点查询）
                    for (Map.Entry<String, String> entry : parameterTree.entrySet()) {
                        if (entry.getKey().startsWith(qn)) {
                            resultValues.add(new CwmpParameterValue(entry.getKey(), entry.getValue(), "xsd:string"));
                        }
                    }
                } else {
                    // 精确参数匹配
                    String val = parameterTree.getOrDefault(qn, "");
                    resultValues.add(new CwmpParameterValue(qn, val, "xsd:string"));
                }
            }
            return CwmpSoapBuilder.buildGetParameterValuesResponse(headerId, resultValues);
        }

        if ("SetParameterValues".equalsIgnoreCase(rpcType)) {
            List<CwmpParameterValue> sets = CwmpSoapParser.parseSetParameterValues(xml);
            for (CwmpParameterValue pv : sets) {
                parameterTree.put(pv.getName(), pv.getValue());
                log.info("[handleAcsRpc][SetParameterValues 更新参数: {} = {}]", pv.getName(), pv.getValue());
            }
            return CwmpSoapBuilder.buildSetParameterValuesResponse(headerId, 0);
        }

        if ("GetParameterNames".equalsIgnoreCase(rpcType)) {
            CwmpGetParameterNamesRequest req = CwmpSoapParser.parseGetParameterNames(xml);
            String targetPath = req != null ? req.getParameterPath() : "";
            boolean nextLevel = req != null && Boolean.TRUE.equals(req.getNextLevel());

            List<CwmpParameterInfo> infos = new ArrayList<>();
            for (String key : parameterTree.keySet()) {
                if (StrUtil.isBlank(targetPath) || key.startsWith(targetPath)) {
                    if (nextLevel && StrUtil.isNotBlank(targetPath)) {
                        String sub = key.substring(targetPath.length());
                        int dotIdx = sub.indexOf('.');
                        if (dotIdx > 0 && dotIdx < sub.length() - 1) {
                            continue; // 过滤多级子节点
                        }
                    }
                    infos.add(new CwmpParameterInfo(key, true));
                }
            }
            return CwmpSoapBuilder.buildGetParameterNamesResponse(headerId, infos);
        }

        if ("Reboot".equalsIgnoreCase(rpcType)) {
            String cmdKey = CwmpSoapParser.parseReboot(xml);
            rebootCount.incrementAndGet();
            log.warn("[handleAcsRpc][执行设备 Reboot 重启, CommandKey: {}]", cmdKey);
            return CwmpSoapBuilder.buildRebootResponse(headerId);
        }

        if ("FactoryReset".equalsIgnoreCase(rpcType)) {
            factoryResetCount.incrementAndGet();
            initDefaultParameters();
            log.warn("[handleAcsRpc][执行设备 FactoryReset 恢复出厂设置]");
            return CwmpSoapBuilder.buildFactoryResetResponse(headerId);
        }

        if ("Download".equalsIgnoreCase(rpcType)) {
            CwmpDownloadRequest dlReq = CwmpSoapParser.parseDownload(xml);
            this.lastDownloadRequest = dlReq;
            downloadCount.incrementAndGet();
            log.info("[handleAcsRpc][接收 Download 固件下载命令: URL={}, FileSize={}]", dlReq.getUrl(), dlReq.getFileSize());
            return CwmpSoapBuilder.buildDownloadResponse(headerId, 1);
        }

        // 默认返回空或未知响应
        log.warn("[handleAcsRpc][未处理的 RPC 指令: {}，返回空 Body]", rpcType);
        return "";
    }

    // ==================== HTTP 底层通信辅助方法 ====================

    private static class HttpResponse {
        int code;
        String body;
        String cookie;

        HttpResponse(int code, String body, String cookie) {
            this.code = code;
            this.body = body;
            this.cookie = cookie;
        }
    }

    private HttpResponse sendHttpPost(String targetUrl, String body, String cookie) throws IOException {
        URL url = new URL(targetUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(10000);
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
        conn.setRequestProperty("User-Agent", "CwmpCpeSimulator/1.0");

        if (StrUtil.isNotBlank(cookie)) {
            conn.setRequestProperty("Cookie", cookie);
        }

        byte[] bytes = StrUtil.nullToEmpty(body).getBytes(StandardCharsets.UTF_8);
        conn.setRequestProperty("Content-Length", String.valueOf(bytes.length));

        try (OutputStream os = conn.getOutputStream()) {
            os.write(bytes);
            os.flush();
        }

        int code = conn.getResponseCode();
        String respCookie = conn.getHeaderField("Set-Cookie");
        if (StrUtil.isNotBlank(respCookie)) {
            int semiIdx = respCookie.indexOf(';');
            if (semiIdx > 0) {
                respCookie = respCookie.substring(0, semiIdx);
            }
        }

        String respBody = "";
        try (InputStream is = (code >= 200 && code < 400) ? conn.getInputStream() : conn.getErrorStream()) {
            if (is != null) {
                respBody = IoUtil.read(is, StandardCharsets.UTF_8);
            }
        }
        return new HttpResponse(code, respBody, respCookie);
    }

    // ==================== Connection Request HTTP Digest 认证服务 ====================

    private class ConnectionRequestHandler implements HttpHandler {
        private final String realm = "CWMP";

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            connectionRequestReceivedCount.incrementAndGet();
            String method = exchange.getRequestMethod();
            log.info("[ConnectionRequestHandler][收到 ACS 唤醒请求: {} {}]", method, exchange.getRequestURI());

            // 1. 检查 Authorization 头
            String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
            if (StrUtil.isBlank(authHeader) || !authHeader.startsWith("Digest ")) {
                // 回复 401 Unauthorized 质询
                String nonce = UUID.fastUUID().toString(true);
                String challenge = String.format("Digest realm=\"%s\", nonce=\"%s\", qop=\"auth\"", realm, nonce);
                exchange.getResponseHeaders().set("WWW-Authenticate", challenge);
                exchange.sendResponseHeaders(401, -1);
                exchange.close();
                return;
            }

            // 2. 验证 Digest 凭证
            boolean authorized = verifyDigest(authHeader, method);
            if (!authorized) {
                log.warn("[ConnectionRequestHandler][Digest 认证失败: {}]", authHeader);
                exchange.sendResponseHeaders(401, -1);
                exchange.close();
                return;
            }

            // 3. 认证成功，回复 200 OK
            log.info("[ConnectionRequestHandler][Digest 认证成功，即将向 ACS 发起 Inform 会话]");
            exchange.sendResponseHeaders(200, 0);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write("OK".getBytes(StandardCharsets.UTF_8));
            }
            exchange.close();

            // 4. 异步触发 Inform (6 CONNECTION REQUEST)
            threadPool.execute(() -> {
                try {
                    Thread.sleep(200); // 稍等片刻确保 ACS 完成唤醒请求的收取
                    sendConnectionRequest();
                } catch (Exception e) {
                    log.error("[ConnectionRequestHandler][异步回连 Inform 异常]", e);
                }
            });
        }

        private boolean verifyDigest(String authHeader, String method) {
            try {
                String paramsStr = authHeader.substring("Digest ".length());
                Map<String, String> map = new HashMap<>();
                for (String part : paramsStr.split(",")) {
                    String[] kv = part.trim().split("=", 2);
                    if (kv.length == 2) {
                        String k = kv[0].trim();
                        String v = kv[1].trim().replace("\"", "");
                        map.put(k, v);
                    }
                }

                String user = map.get("username");
                String nonce = map.get("nonce");
                String uri = map.get("uri");
                String response = map.get("response");
                String qop = map.get("qop");
                String nc = map.get("nc");
                String cnonce = map.get("cnonce");

                if (!crUsername.equals(user)) {
                    return false;
                }

                // 计算 HA1 = MD5(username:realm:password)
                String ha1 = DigestUtil.md5Hex(crUsername + ":" + realm + ":" + crPassword);
                // 计算 HA2 = MD5(method:uri)
                String ha2 = DigestUtil.md5Hex(method + ":" + uri);

                String expected;
                if ("auth".equalsIgnoreCase(qop)) {
                    expected = DigestUtil.md5Hex(ha1 + ":" + nonce + ":" + nc + ":" + cnonce + ":" + qop + ":" + ha2);
                } else {
                    expected = DigestUtil.md5Hex(ha1 + ":" + nonce + ":" + ha2);
                }

                return expected.equalsIgnoreCase(response);
            } catch (Exception e) {
                log.warn("[verifyDigest][解析 Digest 异常: {}]", e.getMessage());
                return false;
            }
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
        String acs = args.length > 0 ? args[0] : "http://127.0.0.1:9292/cwmp";
        String sn = args.length > 1 ? args[1] : "SN_SIM_" + System.currentTimeMillis() % 10000;
        int crPort = args.length > 2 ? Integer.parseInt(args[2]) : 7547;

        CwmpCpeSimulator cpe = new CwmpCpeSimulator(acs, sn, crPort);
        cpe.start();

        System.out.println("==================================================");
        System.out.println("TR-069 CPE 模拟客户端已启动");
        System.out.println("设备序列号: " + sn);
        System.out.println("ACS 服务端: " + acs);
        System.out.println("ConnectionRequest: http://127.0.0.1:" + crPort + cpe.getConnectionRequestPath());
        System.out.println("==================================================");

        // 发起开机 Bootstrap 注册
        System.out.println("正在向 ACS 发起 0 BOOTSTRAP 注册...");
        boolean success = cpe.sendBootstrap();
        System.out.println("Inform 结果: " + (success ? "成功" : "失败"));

        // 保持常驻监听
        Runtime.getRuntime().addShutdownHook(new Thread(cpe::stop));
        Thread.currentThread().join();
    }

}
