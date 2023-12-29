package com.mz.sip;

import com.mz.common.core.enums.ErrCode;
import com.mz.common.core.exception.ProtocolException;
import com.mz.common.json.utils.JsonUtils;
import com.mz.protocol.config.ProtocolConfig;
import com.mz.protocol.core.AbstractProductProtocol;
import com.mz.sip.config.SipConfig;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;

@Slf4j
public class SipProductProtocol extends AbstractProductProtocol {
    private Vertx vertx;
    private String deployedId;
    private CountDownLatch countDownLatch;
    private VertxSipServer vertxMqttServer;

    @Override
    public void create(ProtocolConfig config) {
        super.create(config);
        vertx = Vertx.vertx();
        SipConfig mqttConfig = JsonUtils.parseObject(config.getConfig(), SipConfig.class);
        vertxMqttServer = new VertxSipServer(mqttConfig);
        DefaultSipHandler handler = (DefaultSipHandler) getHandler();
        vertxMqttServer.setHandler(handler);
    }

    @Override
    public void start() {
        try {
            countDownLatch = new CountDownLatch(1);
            Future<String> future = vertx.deployVerticle(vertxMqttServer);
            future.onSuccess((s -> {
                deployedId = s;
                log.info("start mqtt Protocol {}", deployedId);
                countDownLatch.countDown();
            }));
            future.onFailure((e) -> {
                countDownLatch.countDown();
                log.error("start mqtt Protocol failed", e);
            });
            countDownLatch.await();
            future.succeeded();
        } catch (Throwable e) {
            throw new ProtocolException(ErrCode.COMPONENT_START_ERROR, e);
        }
    }

    @Override
    public void stop() throws Exception {
        vertxMqttServer.stop();
        Future<Void> future = vertx.undeploy(deployedId);
        future.onSuccess(unused -> log.info("stop mqtt component success"));
    }

    @Override
    public void destroy() {

    }

}
