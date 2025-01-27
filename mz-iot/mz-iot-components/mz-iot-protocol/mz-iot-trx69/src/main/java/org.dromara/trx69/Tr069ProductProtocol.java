package org.dromara.trx69;

import org.dromara.common.core.enums.ErrCode;
import org.dromara.common.core.exception.ProtocolException;
import org.dromara.common.json.utils.JsonUtils;
import org.dromara.protocol.config.ProtocolConfig;
import org.dromara.protocol.core.AbstractProductProtocol;
import org.dromara.trx69.config.Tr069Config;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;

@Slf4j
public class Tr069ProductProtocol extends AbstractProductProtocol {
    private Vertx vertx;
    private String deployedId;
    private CountDownLatch countDownLatch;
    private VertxTr069Server vertxMqttServer;

    @Override
    public void create(ProtocolConfig config) {
        super.create(config);
        vertx = Vertx.vertx();
        Tr069Config mqttConfig = JsonUtils.parseObject(config.getConfig(), Tr069Config.class);
        vertxMqttServer = new VertxTr069Server(mqttConfig);
        DefaultTr069Handler handler = (DefaultTr069Handler) getHandler();
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
