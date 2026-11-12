package cn.iocoder.yudao.module.iot.core.protocol.cwmp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TR-069 GetParameterNames 请求对象
 *
 * @author 芋道源码
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CwmpGetParameterNamesRequest {

    /**
     * 参数路径（如 "InternetGatewayDevice." 或 "Device.WiFi."）
     */
    private String parameterPath;

    /**
     * 是否只返回下一层级 (false=递归返回所有下级)
     */
    private Boolean nextLevel;

}
