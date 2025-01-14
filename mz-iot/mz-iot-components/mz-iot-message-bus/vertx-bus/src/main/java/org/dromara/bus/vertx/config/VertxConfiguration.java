package org.dromara.bus.vertx.config;

import org.dromara.bus.core.Pub;
import org.dromara.bus.core.Sub;
import org.dromara.bus.message.BaseMessage;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.mqtt.MqttServerOptions;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.dromara.bus.vertx.VertxPub;
import org.dromara.bus.vertx.VertxSub;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;


@Slf4j
@Configuration
public class VertxConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "vertx")
    public VertxOptions vertxOptions() {
        return new VertxOptions();
    }

    @Bean
    @ConfigurationProperties(prefix = "vertx.mqtt")
    public MqttServerOptions mqttServerOptions() {
        return new MqttServerOptions();
    }

    @Bean
    @ConfigurationProperties(prefix = "vertx.cluster.config")
    public Map<String, Object> zookeeperConfig() {
        return new HashMap<>();
    }

    @Bean
    @SneakyThrows
    public Vertx vertx() {
        VertxOptions vertxOptions = vertxOptions();
        log.debug("init vertx : \n{}", vertxOptions);
        Vertx vertx;
//        if (vertxOptions.isClustered()) {
//            JsonObject config = new JsonObject(zookeeperConfig());
//            log.info("use zookeeper config:\n{}", config);
//            ClusterManager clusterManager = new ZookeeperClusterManager(config);
//            vertxOptions.setClusterManager(clusterManager);
//            CountDownLatch clusterLatch = new CountDownLatch(1);
//            AtomicReference<Throwable> errorReference = new AtomicReference<>();
//            AtomicReference<Vertx> vertxAtomicReference = new AtomicReference<>();
//            Vertx.clusteredVertx(vertxOptions, e -> {
//                try {
//                    if (e.succeeded()) {
//                        log.debug("init clustered vertx success");
//                        vertxAtomicReference.set(e.result());
//                    } else {
//                        errorReference.set(e.cause());
//                    }
//                } finally {
//                    clusterLatch.countDown();
//                }
//            });
//            boolean success = clusterLatch.await(1, TimeUnit.MINUTES);
//            if (!success) {
//                log.warn("wait vertx init timeout!");
//            }
//            if (errorReference.get() != null) {
//                throw errorReference.get();
//            }
//            vertx = vertxAtomicReference.get();
//        } else {
            vertx = Vertx.vertx(vertxOptions);
//        }
        return vertx;
    }

    @ConditionalOnMissingBean
    @Bean
    public Pub<BaseMessage> getThingModelMessageProducer() {
        return new VertxPub<>(BaseMessage.class);
    }

    @ConditionalOnMissingBean
    @Bean
    public Sub<BaseMessage> getThingModelMessageConsumer() {
        return new VertxSub<>(BaseMessage.class);
    }
}
