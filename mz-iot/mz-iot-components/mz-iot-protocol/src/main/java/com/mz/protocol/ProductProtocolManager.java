package com.mz.protocol;

import com.mz.protocol.core.IProductProtocol;
import com.mz.protocol.core.IScriptEngine;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class ProductProtocolManager {
    private final Map<String, IProductProtocol> components = new HashMap<>();
    private final Map<String, Boolean> states = new HashMap<>();

    private IScriptEngine scriptEngine;

    @PostConstruct
    public void init() {

    }

    public void register(String id, IProductProtocol protocol) {
        components.put(protocol.getId(), protocol);
        states.put(protocol.getId(), false);
    }

    public void unregister(String id) {
        IProductProtocol component = components.remove(id);
        states.remove(id);
        if (component == null) {
            return;
        }
        component.stop();
        component.destroy();
    }

    public void start(String id) {
        IProductProtocol component = components.get(id);
        if (component == null) {
            return;
        }

        ProductMessageHandler messageHandler = new ProductMessageHandler(
            this, component,
            scriptEngine,
            component.getScript(), component.getCodec());

        component.setHandler(messageHandler);
        component.start();
        states.put(id, true);
    }

    public void stop(String id) {
        IProductProtocol component = components.get(id);
        if (component == null) {
            return;
        }
        component.stop();
        states.put(id, false);
    }

    public boolean isRunning(String id) {
        return states.containsKey(id) && states.get(id);
    }
}
