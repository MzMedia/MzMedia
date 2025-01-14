package org.dromara.sip.config;

import lombok.Data;

@Data
public class SipConfig {
    private int port;
    private String sslKey;
    private String sslCert;
    private boolean ssl;
    private boolean useWebSocket;
}
