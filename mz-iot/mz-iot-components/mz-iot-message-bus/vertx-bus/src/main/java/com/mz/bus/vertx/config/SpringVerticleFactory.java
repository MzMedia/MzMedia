package com.mz.bus.vertx.config;

import io.vertx.core.Verticle;
import io.vertx.core.spi.VerticleFactory;
import org.springframework.stereotype.Component;
import org.springframework.context.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;

@Component
public class SpringVerticleFactory implements VerticleFactory {

    @Autowired
    private ApplicationContext context;

    @Override
    public String prefix() {
        return "spring";
    }

    @Override
    public Verticle createVerticle(String verticleName, ClassLoader classLoader) throws Exception {
        verticleName = VerticleFactory.removePrefix(verticleName);
        return context.getBean(verticleName, Verticle.class);
    }
}
