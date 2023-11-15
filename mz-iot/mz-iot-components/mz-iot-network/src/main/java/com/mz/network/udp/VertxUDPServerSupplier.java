package com.mz.network.udp;

import com.mz.bus.vertx.config.VerticleSupplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import static org.springframework.beans.factory.config.AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE;


@Component
public class VertxUDPServerSupplier implements VerticleSupplier {
    @Autowired
    private ApplicationContext context;

    @Override
    public int getInstances() {
        return 1;
    }

    @Override
    public VertxUDPServer get() {
        //使用spring上下文创建实例,并注入spring中的bean
        return (VertxUDPServer) context.getAutowireCapableBeanFactory()
                .autowire(VertxUDPServer.class, AUTOWIRE_BY_TYPE, true);
    }

    @Override
    public String toString() {
        return "UDPServer(" + getInstances() + ")";
    }
}
