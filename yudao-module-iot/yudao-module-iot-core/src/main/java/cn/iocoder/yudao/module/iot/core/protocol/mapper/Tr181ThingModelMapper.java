package cn.iocoder.yudao.module.iot.core.protocol.mapper;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.module.iot.core.protocol.cwmp.model.CwmpParameterValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TR-181 / TR-098 宽带数据模型与 IoT 统一物模型 (TSL) 映射器
 *
 * 将 CPE 上报的海量复杂路径参数，提取核心状态指标，映射为平台统一物模型属性
 *
 * @author 芋道源码
 */
public class Tr181ThingModelMapper {

    /**
     * 将 CWMP 上报的参数列表映射为物模型属性 Map
     */
    public static Map<String, Object> mapCwmpParamsToThingModel(List<CwmpParameterValue> parameterList) {
        Map<String, Object> properties = new HashMap<>();
        if (parameterList == null || parameterList.isEmpty()) {
            return properties;
        }

        for (CwmpParameterValue pv : parameterList) {
            String path = pv.getName();
            String val = pv.getValue();
            if (StrUtil.isBlank(path) || val == null) {
                continue;
            }
            mapSingleParam(path, val, properties);
        }
        return properties;
    }

    /**
     * 将 USP GetResp / Notify 参数映射为物模型属性 Map
     */
    public static Map<String, Object> mapUspParamsToThingModel(Map<String, String> uspParams) {
        Map<String, Object> properties = new HashMap<>();
        if (uspParams == null || uspParams.isEmpty()) {
            return properties;
        }
        for (Map.Entry<String, String> entry : uspParams.entrySet()) {
            mapSingleParam(entry.getKey(), entry.getValue(), properties);
        }
        return properties;
    }

    private static void mapSingleParam(String path, String val, Map<String, Object> properties) {
        // 1. 设备基本信息
        if (path.endsWith("DeviceInfo.Manufacturer")) {
            properties.put("manufacturer", val);
        } else if (path.endsWith("DeviceInfo.ModelName")) {
            properties.put("modelName", val);
        } else if (path.endsWith("DeviceInfo.SoftwareVersion")) {
            properties.put("firmwareVersion", val);
        } else if (path.endsWith("DeviceInfo.HardwareVersion")) {
            properties.put("hardwareVersion", val);
        } else if (path.endsWith("DeviceInfo.SerialNumber")) {
            properties.put("serialNumber", val);
        } else if (path.endsWith("DeviceInfo.UpTime")) {
            if (NumberUtil.isLong(val)) {
                properties.put("upTime", Long.parseLong(val));
            }
        }
        // 2. WAN 状态与 IP
        else if (path.endsWith("ExternalIPAddress") || path.endsWith("IPv4Address.1.IPAddress")) {
            properties.put("wanIp", val);
        } else if (path.endsWith("ConnectionStatus") || path.endsWith("WANIPConnection.1.ConnectionStatus")) {
            properties.put("wanStatus", val);
        }
        // 3. Wi-Fi 状态
        else if (path.endsWith("WLANConfiguration.1.SSID") || path.endsWith("WiFi.SSID.1.SSID")) {
            properties.put("wifiSsid", val);
        } else if (path.endsWith("WLANConfiguration.1.Status") || path.endsWith("WiFi.Radio.1.Status")) {
            properties.put("wifiStatus", val);
        } else if (path.endsWith("WLANConfiguration.1.Channel") || path.endsWith("WiFi.Radio.1.Channel")) {
            if (NumberUtil.isInteger(val)) {
                properties.put("wifiChannel", Integer.parseInt(val));
            }
        } else if (path.endsWith("WLANConfiguration.1.Enable") || path.endsWith("WiFi.SSID.1.Enable")) {
            properties.put("wifiEnable", "1".equals(val) || "true".equalsIgnoreCase(val));
        }
        // 4. 内存与 CPU 使用率 (若设备支持)
        else if (path.endsWith("MemoryStatus.Free") || path.endsWith("DeviceInfo.MemoryStatus.Free")) {
            if (NumberUtil.isLong(val)) {
                properties.put("freeMemory", Long.parseLong(val));
            }
        } else if (path.endsWith("CPUUsage") || path.endsWith("DeviceInfo.ProcessStatus.CPUUsage")) {
            if (NumberUtil.isNumber(val)) {
                properties.put("cpuUsage", Double.parseDouble(val));
            }
        }
    }

}
