package cn.iocoder.yudao.module.iot.core.protocol.usp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * USP Operate 操作命令请求结构 (例如 Device.Reboot(), Device.Download())
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UspOperateRequest {

    /**
     * 命令路径，例如 "Device.Reboot()" 或 "Device.IP.Diagnostics.IPPing()"
     */
    private String command;

    /**
     * 命令标识 Key
     */
    private String commandKey;

    /**
     * 是否要求返回响应
     */
    @Builder.Default
    private Boolean sendResp = true;

    /**
     * 命令入参 (Key -> Value)
     */
    @Builder.Default
    private Map<String, String> inputArgs = new HashMap<>();

}
