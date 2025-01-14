package org.dromara.network.codec;


import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;

public abstract class AbstractProtocolHandler<T, R> extends AbstractVerticle implements Handler<Message<T>>, InvokeHandler<R>{
    @Override
    public void start() throws Exception {
        vertx.eventBus().consumer(getTopic(), this::handle);
    }
    protected abstract String getTopic();

    protected abstract String getMessageType();

}
