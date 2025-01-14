package org.dromara.network.client.message;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;
import lombok.Data;


@Data
public class BaseMessage extends Message {
    private String clientId;
    private String productID;
    private String deviceSN;
    private String type;
    private int code;
    private NetSocket socket;
    private Buffer buffer;
}
