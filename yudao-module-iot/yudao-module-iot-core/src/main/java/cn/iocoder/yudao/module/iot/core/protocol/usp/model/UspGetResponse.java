package cn.iocoder.yudao.module.iot.core.protocol.usp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * USP Get 响应结构
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UspGetResponse {

    /**
     * 解析到的参数路径与值映射 (path -> value)
     */
    @Builder.Default
    private Map<String, String> entries = new HashMap<>();

}
