package cn.iocoder.yudao.module.iot.core.protocol.usp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * USP Set 请求结构
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UspSetRequest {

    /**
     * 要更新的参数与目标值 (path -> value)
     */
    @Builder.Default
    private Map<String, String> params = new HashMap<>();

    /**
     * 是否允许部分成功
     */
    @Builder.Default
    private Boolean allowPartial = true;

}
