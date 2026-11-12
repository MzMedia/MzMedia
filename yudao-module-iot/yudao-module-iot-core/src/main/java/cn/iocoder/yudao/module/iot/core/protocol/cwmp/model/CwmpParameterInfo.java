package cn.iocoder.yudao.module.iot.core.protocol.cwmp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TR-069 参数信息结构 (ParameterInfoStruct，用于 GetParameterNamesResponse)
 *
 * @author 芋道源码
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CwmpParameterInfo {

    /**
     * 参数路径，如 InternetGatewayDevice.DeviceInfo.
     */
    private String name;

    /**
     * 是否可写
     */
    private Boolean writable;

}
