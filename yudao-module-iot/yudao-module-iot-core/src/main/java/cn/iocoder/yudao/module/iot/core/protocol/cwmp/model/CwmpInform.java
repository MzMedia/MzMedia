package cn.iocoder.yudao.module.iot.core.protocol.cwmp.model;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * TR-069 CPE 主动上报 Inform 数据模型
 *
 * @author 芋道源码
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CwmpInform {

    /**
     * SOAP Header 中的消息 ID
     */
    private String headerId;

    /**
     * 设备标识
     */
    private CwmpDeviceId deviceId;

    /**
     * 事件列表
     */
    @Builder.Default
    private List<CwmpEvent> events = new ArrayList<>();

    /**
     * 周期当前时间
     */
    private String currentTime;

    /**
     * 重试次数
     */
    private Integer retryCount;

    /**
     * 上报的参数键值对
     */
    @Builder.Default
    private List<CwmpParameterValue> parameterList = new ArrayList<>();

    /**
     * @return 序列号
     */
    public String getSerialNumber() {
        return deviceId != null ? deviceId.getSerialNumber() : null;
    }

    /**
     * @return 制造商
     */
    public String getManufacturer() {
        return deviceId != null ? deviceId.getManufacturer() : null;
    }

    /**
     * @return 产品类型
     */
    public String getProductClass() {
        return deviceId != null ? deviceId.getProductClass() : null;
    }

    /**
     * @return 软件版本
     */
    public String getSoftwareVersion() {
        return findParameterValueEndingWith("DeviceInfo.SoftwareVersion");
    }

    /**
     * @return 硬件版本
     */
    public String getHardwareVersion() {
        return findParameterValueEndingWith("DeviceInfo.HardwareVersion");
    }

    /**
     * @return ConnectionRequestURL
     */
    public String getConnectionRequestUrl() {
        return findParameterValueEndingWith("ManagementServer.ConnectionRequestURL");
    }

    /**
     * @return 外网 IP 地址
     */
    public String getExternalIpAddress() {
        String ip = findParameterValueEndingWith("ExternalIPAddress");
        if (StrUtil.isBlank(ip)) {
            ip = findParameterValueEndingWith("IPv4Address.1.IPAddress");
        }
        return ip;
    }

    /**
     * @return 数据模型类型 (TR098 或 TR181)
     */
    public String getDataModelType() {
        if (parameterList == null || parameterList.isEmpty()) {
            return "TR181";
        }
        for (CwmpParameterValue pv : parameterList) {
            if (pv.getName() != null) {
                if (pv.getName().startsWith("InternetGatewayDevice")) {
                    return "TR098";
                } else if (pv.getName().startsWith("Device")) {
                    return "TR181";
                }
            }
        }
        return "TR181";
    }

    /**
     * @return 是否包含首次连接 BOOTSTRAP 事件
     */
    public boolean hasBootstrapEvent() {
        return hasEventCode("0 BOOTSTRAP");
    }

    /**
     * @return 是否包含开机 BOOT 事件
     */
    public boolean hasBootEvent() {
        return hasEventCode("1 BOOT");
    }

    /**
     * @return 是否包含周期上报 PERIODIC 事件
     */
    public boolean hasPeriodicEvent() {
        return hasEventCode("2 PERIODIC");
    }

    /**
     * @return 是否包含反向唤醒 CONNECTION REQUEST 事件
     */
    public boolean hasConnectionRequestEvent() {
        return hasEventCode("6 CONNECTION REQUEST");
    }

    public boolean hasEventCode(String code) {
        if (events == null || events.isEmpty()) {
            return false;
        }
        for (CwmpEvent event : events) {
            if (code.equalsIgnoreCase(event.getEventCode())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 辅助查找匹配后缀的参数值
     */
    public String findParameterValueEndingWith(String suffix) {
        if (parameterList == null) {
            return null;
        }
        for (CwmpParameterValue pv : parameterList) {
            if (pv.getName() != null && pv.getName().endsWith(suffix)) {
                return pv.getValue();
            }
        }
        return null;
    }

}
