package org.dromara.network.codec;

import org.dromara.network.client.message.BaseMessage;
import io.vertx.core.Promise;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Protocol("Test")
public class TestCodec extends AbstractProtocolCodec {
    @Override
    protected List<AbstractProtocolHandler> getHandlers() {
        return Arrays.asList(new ReadHandler(), new WriteHandler());
    }

    @Override
    protected void decode(BaseMessage message) {

    }

    @Override
    protected String getLogicAddress(BaseMessage message) {
        return null;
    }

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        super.start(startPromise);
    }

    @Override
    public void stop(Promise<Void> stopPromise) throws Exception {
        super.stop(stopPromise);
    }
}
