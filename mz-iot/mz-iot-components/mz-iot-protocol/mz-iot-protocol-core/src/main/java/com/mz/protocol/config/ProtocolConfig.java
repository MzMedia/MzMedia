package com.mz.protocol.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProtocolConfig {
    private long cmdTimeout;

    private String config;
}
