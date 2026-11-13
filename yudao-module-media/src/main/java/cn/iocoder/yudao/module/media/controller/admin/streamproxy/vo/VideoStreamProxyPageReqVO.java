package cn.iocoder.yudao.module.media.controller.admin.streamproxy.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 拉流代理分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class VideoStreamProxyPageReqVO extends PageParam {

    @Schema(description = "应用名", example = "live")
    private String app;

    @Schema(description = "流 ID，支持模糊搜索", example = "ch01")
    private String stream;

    @Schema(description = "拉流名称，支持模糊搜索", example = "园区大门")
    private String name;

    @Schema(description = "是否启用 (0未启用 1启用)", example = "1")
    private Integer enable;
}
