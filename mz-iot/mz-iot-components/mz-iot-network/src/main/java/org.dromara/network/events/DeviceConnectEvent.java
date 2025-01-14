package org.dromara.network.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.Date;



@Getter
public class DeviceConnectEvent extends ApplicationEvent {
    private final String clientId;
    private final Date connectTime;

    public DeviceConnectEvent(Object source,String clientId,Date connectTime) {
        super(source);
        this.clientId = clientId;
        this.connectTime = connectTime;
    }
}
