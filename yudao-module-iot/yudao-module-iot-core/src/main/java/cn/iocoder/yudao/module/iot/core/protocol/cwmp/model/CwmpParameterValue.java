package cn.iocoder.yudao.module.iot.core.protocol.cwmp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TR-069 参数值结构 (ParameterValueStruct)
 *
 * @author 芋道源码
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CwmpParameterValue {

    /**
     * 参数路径，如 InternetGatewayDevice.DeviceInfo.SoftwareVersion
     */
    private String name;

    /**
     * 参数值
     */
    private String value;

    /**
     * 参数数据类型，如 xsd:string, xsd:unsignedInt, xsd:boolean, xsd:dateTime
     */
    private String type;

}
