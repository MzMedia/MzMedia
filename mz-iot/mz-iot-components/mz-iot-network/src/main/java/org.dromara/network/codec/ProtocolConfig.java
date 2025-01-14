package org.dromara.network.codec;

import lombok.Data;

@Data
public class ProtocolConfig {
    private String protocol;
    private String codec;
    private String session;
    private String host;
    private int port;
    private String name;
}
