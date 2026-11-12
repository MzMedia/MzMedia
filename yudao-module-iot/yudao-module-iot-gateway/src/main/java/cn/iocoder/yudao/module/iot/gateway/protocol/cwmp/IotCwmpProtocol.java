package cn.iocoder.yudao.module.iot.gateway.protocol.cwmp;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.iocoder.yudao.module.iot.core.enums.IotProtocolTypeEnum;
import cn.iocoder.yudao.module.iot.core.messagebus.core.IotMessageBus;
import cn.iocoder.yudao.module.iot.core.util.IotDeviceMessageUtils;
import cn.iocoder.yudao.module.iot.gateway.config.IotGatewayProperties;
import cn.iocoder.yudao.module.iot.gateway.protocol.IotProtocol;
import cn.iocoder.yudao.module.iot.gateway.protocol.cwmp.session.IotCwmpSessionManager;
import cn.iocoder.yudao.module.iot.gateway.service.device.IotDeviceService;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

/**
 * TR-069 (CWMP) ACS 协议实现
 *
 * 基于 Vert.x 异步非阻塞 HTTP 服务器实现电信级 Auto Configuration Server
 *
 * @author 芋道源码
 */
@Slf4j
public class IotCwmpProtocol implements IotProtocol {

    private final IotGatewayProperties.ProtocolProperties properties;

    @Getter
    private final String serverId;

    @Getter
    private volatile boolean running = false;

    @Getter
    private Vertx vertx;
    private HttpServer httpServer;

    @Getter
    private final IotCwmpSessionManager sessionManager;
    @Getter
    private final IotCwmpConnectionRequestClient connectionRequestClient;

    private final IotCwmpConfig config;
    private IotCwmpDownstreamSubscriber downstreamSubscriber;

    public IotCwmpProtocol(IotGatewayProperties.ProtocolProperties properties) {
        this.properties = properties;
        this.serverId = IotDeviceMessageUtils.generateServerId(properties.getPort());
        this.config = properties.getCwmp() != null ? properties.getCwmp() : new IotCwmpConfig();
        this.sessionManager = new IotCwmpSessionManager();
        this.connectionRequestClient = new IotCwmpConnectionRequestClient();
    }

    @Override
    public String getId() {
        return properties.getId();
    }

    @Override
    public IotProtocolTypeEnum getType() {
        return IotProtocolTypeEnum.TR069;
    }

    @Override
    public void start() {
        if (running) {
            log.warn("[start][TR-069 CWMP 协议 {} 已经在运行中]", getId());
            return;
        }

        this.vertx = Vertx.vertx();

        // 1. 创建路由
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());

        // 2. 鉴权处理器
        if (Boolean.TRUE.equals(config.getAuthEnabled())) {
            router.route().handler(new IotCwmpAuthHandler(config));
        }

        // 3. 上行消息处理器
        IotMessageBus messageBus = SpringUtil.getBean(IotMessageBus.class);
        IotCwmpUpstreamHandler upstreamHandler = new IotCwmpUpstreamHandler(this, sessionManager, messageBus);

        String path = StrUtil.blankToDefault(config.getPath(), "/acs");
        router.post(path).handler(upstreamHandler);
        if (!"/cwmp".equalsIgnoreCase(path)) {
            router.post("/cwmp").handler(upstreamHandler);
        }

        // 4. 下行消息订阅者
        IotDeviceService deviceService = SpringUtil.getBean(IotDeviceService.class);
        this.downstreamSubscriber = new IotCwmpDownstreamSubscriber(
                this, messageBus, sessionManager, connectionRequestClient, deviceService);
        downstreamSubscriber.start();

        // 5. 启动 HTTP 服务
        HttpServerOptions options = new HttpServerOptions();
        options.setPort(properties.getPort());
        this.httpServer = vertx.createHttpServer(options);
        httpServer.requestHandler(router);

        httpServer.listen().onComplete(res -> {
            if (res.succeeded()) {
                running = true;
                log.info("[start][TR-069 ACS 服务器启动成功，端口：{}，监听路径：{}]", properties.getPort(), path);
            } else {
                running = false;
                log.error("[start][TR-069 ACS 服务器启动失败，端口：{}]", properties.getPort(), res.cause());
            }
        });

        // 6. 定时清理过期会话 (每分钟一次)
        vertx.setPeriodic(60000, id -> sessionManager.cleanExpiredSessions(config.getSessionTimeoutMs()));
    }

    @Override
    public void stop() {
        if (!running) {
            return;
        }
        if (downstreamSubscriber != null) {
            downstreamSubscriber.stop();
        }
        if (httpServer != null) {
            httpServer.close().onComplete(res -> log.info("[stop][TR-069 ACS HTTP 服务器已关闭]"));
        }
        if (vertx != null) {
            vertx.close();
        }
        running = false;
        log.info("[stop][TR-069 协议 {} 已停止]", getId());
    }

    /**
     * 执行同步/异步 CWMP SOAP RPC
     *
     * @param serialNumber 设备序列号
     * @param soapXml SOAP XML 报文
     * @return 响应结果 XML Future
     */
    public CompletableFuture<String> executeRpc(String serialNumber, String soapXml) {
        long timeout = config.getRpcTimeoutMs() != null ? config.getRpcTimeoutMs() : 30000L;
        return sessionManager.enqueueCommand(serialNumber, soapXml, timeout);
    }

}
