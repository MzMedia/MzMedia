package cn.iocoder.yudao.module.iot.core.protocol.cwmp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * TR-069 错误异常 (SOAP Fault / CWMP Fault)
 *
 * @author 芋道源码
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CwmpFault {

    /**
     * 错误代码，例如 9000 (Method not supported), 9001 (Request denied), 9002 (Internal error), 9003 (Invalid arguments)
     */
    private Integer faultCode;

    /**
     * 错误描述
     */
    private String faultString;

    /**
     * 参数级别的错误详情
     */
    private List<ParameterFault> setParameterValuesFaults;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ParameterFault {
        private String parameterName;
        private Integer faultCode;
        private String faultString;
    }

}
