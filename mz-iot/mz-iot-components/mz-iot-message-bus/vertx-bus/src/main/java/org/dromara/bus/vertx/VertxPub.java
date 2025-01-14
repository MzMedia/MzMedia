package org.dromara.bus.vertx;

import org.dromara.bus.core.Pub;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import lombok.SneakyThrows;

import java.util.concurrent.CountDownLatch;

public class VertxPub<T> implements Pub<T> {
    private final MqProducerVerticle<T> producerVerticle;

    private final CountDownLatch countDownLatch = new CountDownLatch(1);

    @SneakyThrows
    public VertxPub(Class<T> cls) {
        producerVerticle = new MqProducerVerticle<>(cls);
        VertxManager.getVertx().deployVerticle(producerVerticle, stringAsyncResult -> countDownLatch.countDown());
        //等待初始化完成
        countDownLatch.await();
    }

    @Override
    public void publish(String topic, T msg) {
        producerVerticle.publish(topic, msg);
    }

    public static class MqProducerVerticle<T> extends AbstractVerticle {

        private final Class<T> cls;
        private EventBus eventBus;

        public MqProducerVerticle(Class<T> cls) {
            this.cls = cls;
        }

        @Override
        public void start() {
            eventBus = vertx.eventBus();
            eventBus.registerCodec(new BeanCodec<>(cls));
        }

        public void publish(String topic, T msg) {
            eventBus.publish(topic, msg, new DeliveryOptions().setCodecName(cls.getSimpleName()));
        }
    }
}
