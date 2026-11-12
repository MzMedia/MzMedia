package cn.iocoder.yudao.module.iot.core.protocol.usp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * USP Operate 操作命令响应结构
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UspOperateResponse {

    /**
     * 是否执行成功
     */
    @Builder.Default
    private Boolean executed = true;

    /**
     * 命令输出参数 (Key -> Value)
     */
    @Builder.Default
    private Map<String, String> outputArgs = new HashMap<>();

}
