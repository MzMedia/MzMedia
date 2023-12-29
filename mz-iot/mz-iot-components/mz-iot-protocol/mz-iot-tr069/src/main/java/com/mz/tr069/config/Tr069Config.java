package com.mz.tr069.config;

import lombok.Data;

@Data
public class Tr069Config {
    private int port;
    private String sslKey;
    private String sslCert;
    private boolean ssl;
    private boolean useWebSocket;
}
