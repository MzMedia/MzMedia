package org.dromara.network.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.Date;



@Getter
public class DeviceDisconnectEvent extends ApplicationEvent {
    private final String clientId;
    private final Date disconnectTime;

    public DeviceDisconnectEvent(Object source,String clientId,Date disconnectTime) {
        super(source);
        this.clientId = clientId;
        this.disconnectTime = disconnectTime;
    }
}
