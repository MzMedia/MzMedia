package cn.iocoder.yudao.module.media.controller.admin.device.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 视频设备分页查询 Request VO
 *
 * @author MzMedia Architect
 */
@Schema(description = "管理后台 - 视频设备分页查询 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class VideoDevicePageReqVO extends PageParam {

    @Schema(description = "设备国标编码 (支持模糊搜索)", example = "3402000000")
    private String deviceId;

    @Schema(description = "设备名称 (支持模糊搜索)", example = "大门东侧摄像头")
    private String name;

    @Schema(description = "设备在线状态 (0: 离线, 1: 在线)", example = "1")
    private Integer onLine;

    @Schema(description = "流传输模式", example = "UDP")
    private String streamMode;
}
