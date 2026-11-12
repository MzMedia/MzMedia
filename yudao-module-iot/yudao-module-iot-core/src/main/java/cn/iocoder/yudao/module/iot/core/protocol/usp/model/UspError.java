package cn.iocoder.yudao.module.iot.core.protocol.usp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * TR-369 USP Error 结构
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UspError {

    /**
     * 错误码 (USP 7000 系列标准错误码)
     */
    private Integer errCode;

    /**
     * 错误信息描述
     */
    private String errMsg;

    /**
     * 参数级别的错误详情 (Path -> ErrorMsg)
     */
    @Builder.Default
    private Map<String, String> paramErrors = new HashMap<>();

}
