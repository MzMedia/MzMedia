package cn.iocoder.yudao.module.iot.core.protocol.usp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * USP Get 请求结构
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UspGetRequest {

    /**
     * 要查询的参数或对象路径列表
     */
    @Builder.Default
    private List<String> paramPaths = new ArrayList<>();

    /**
     * 最大查询深度（0 表示无限）
     */
    @Builder.Default
    private Integer maxDepth = 1;

}
