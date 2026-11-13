package cn.iocoder.yudao.module.media.controller.admin.mediaserver.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 流媒体节点分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class VideoMediaServerPageReqVO extends PageParam {

    @Schema(description = "节点 ID，支持模糊搜索", example = "zlm-node-01")
    private String id;

    @Schema(description = "节点 IP", example = "192.168.1.100")
    private String ip;

    @Schema(description = "在线状态 (0离线 1在线)", example = "1")
    private Integer status;
}
