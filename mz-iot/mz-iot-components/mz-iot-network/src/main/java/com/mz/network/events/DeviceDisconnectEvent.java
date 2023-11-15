package com.mz.network.events;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;


@AllArgsConstructor
@Getter
public class DeviceDisconnectEvent {
    private String clientId;

    private Date disconnectTime;
}
