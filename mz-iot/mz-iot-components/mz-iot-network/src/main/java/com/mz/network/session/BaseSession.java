package com.mz.network.session;

import io.vertx.core.net.NetSocket;
import lombok.Data;

@Data
public class BaseSession {
    private String sessionId;
    private String logicAddress;
    private NetSocket socket;
}
