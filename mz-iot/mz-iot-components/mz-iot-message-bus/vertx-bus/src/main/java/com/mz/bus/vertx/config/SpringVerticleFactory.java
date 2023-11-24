package com.mz.bus.vertx.config;

import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.spi.VerticleFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class SpringVerticleFactory implements VerticleFactory {

    @Autowired
    private ApplicationContext context;

    @Override
    public int order() {
        return VerticleFactory.super.order();
    }

    @Override
    public void init(Vertx vertx) {
        VerticleFactory.super.init(vertx);
    }

    @Override
    public void close() {
        VerticleFactory.super.close();
    }

    @Override
    public String prefix() {
        return "spring";
    }


    public Verticle createVerticle(String verticleName, ClassLoader classLoader) throws Exception {
        verticleName = VerticleFactory.removePrefix(verticleName);
        return context.getBean(verticleName, Verticle.class);
    }
}
