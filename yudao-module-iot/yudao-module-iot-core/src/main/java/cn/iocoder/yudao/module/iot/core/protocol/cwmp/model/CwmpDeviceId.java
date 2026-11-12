package cn.iocoder.yudao.module.iot.core.protocol.cwmp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TR-069 CPE 设备标识信息 (DeviceId)
 *
 * @author 芋道源码
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CwmpDeviceId {

    /**
     * 制造商 (e.g. Huawei, ZTE, ADB Broadband)
     */
    private String manufacturer;

    /**
     * 制造商组织唯一标识符 (6位十六进制, e.g. 0013C8)
     */
    private String oui;

    /**
     * 产品类别/型号 (e.g. HG8245, EchoLife, VV5522)
     */
    private String productClass;

    /**
     * 设备序列号 (SerialNumber)
     */
    private String serialNumber;

}
