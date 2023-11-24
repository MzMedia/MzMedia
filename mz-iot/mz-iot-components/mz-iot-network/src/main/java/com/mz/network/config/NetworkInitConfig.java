package com.mz.network.config;

import com.mz.bus.vertx.config.VerticleSupplier;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.spi.VerticleFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Slf4j
@Configuration
public class NetworkInitConfig {
    @Bean
    public VerticleRegisterProcessor startMqttServerProcessor() {
        return new VerticleRegisterProcessor();
    }

    public static class VerticleRegisterProcessor implements CommandLineRunner {

        @Autowired
        private VerticleFactory verticleFactory;

        @Autowired
        private List<VerticleSupplier> verticles;

        @Autowired
        private Vertx vertx;

        @Override
        public void run(String... args) throws Exception {
            vertx.registerVerticleFactory(verticleFactory);
            for (VerticleSupplier suplier : verticles) {
                DeploymentOptions options = new DeploymentOptions();
                options.setHa(true);
                options.setInstances(suplier.getInstances());
                vertx.deployVerticle(suplier, options, e -> {
                    if (!e.succeeded()) {
                        log.error("deploy verticle :{} {} {} error", suplier, e.succeeded(), e.cause());
                    } else {
                        log.debug("deploy verticle :{} success",suplier);
                    }
                });
            }
        }
    }
}
