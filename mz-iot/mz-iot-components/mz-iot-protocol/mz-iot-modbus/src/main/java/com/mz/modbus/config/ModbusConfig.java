package com.mz.modbus.config;

import lombok.Data;

@Data
public class ModbusConfig {
    private int port;
    private String sslKey;
    private String sslCert;
    private boolean ssl;
    private boolean useWebSocket;
}
