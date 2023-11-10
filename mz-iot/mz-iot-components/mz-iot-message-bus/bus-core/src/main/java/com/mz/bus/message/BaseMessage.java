package com.mz.bus.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BaseMessage {
    private String msgid;
    private String deviceSn;
    private String deviceName;
}
