package cn.iocoder.yudao.module.iot.core.protocol.usp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * USP Set 响应结构
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UspSetResponse {

    /**
     * 是否执行成功
     */
    @Builder.Default
    private Boolean success = true;

    /**
     * 更新后的参数值确认
     */
    @Builder.Default
    private Map<String, String> updatedParams = new HashMap<>();

}
