package org.dromara.bus.mqtt;

import org.dromara.bus.core.Sub;
import org.dromara.bus.core.SubCallback;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Data
@NoArgsConstructor
public class MqttSub<T> implements Sub<T> {

    @Autowired
    private MqttCallback mqttCallback;

    @Override
    public void sub(String route, SubCallback<T> handler) {
        mqttCallback.put(route, handler);
    }
}
