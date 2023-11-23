package com.mz.bus.mqtt;

import com.mz.bus.core.Sub;
import com.mz.bus.core.SubCallback;
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
