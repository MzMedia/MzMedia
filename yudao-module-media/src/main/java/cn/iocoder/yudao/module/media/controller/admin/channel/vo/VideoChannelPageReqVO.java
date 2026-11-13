package cn.iocoder.yudao.module.media.controller.admin.channel.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 视频通道分页查询 Request VO
 *
 * @author MzMedia Architect
 */
@Schema(description = "管理后台 - 视频通道分页查询 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class VideoChannelPageReqVO extends PageParam {

    @Schema(description = "所属设备国标编号", example = "34020000001110000001")
    private String deviceId;

    @Schema(description = "通道国标编码 (支持模糊搜索)", example = "34020000001320000001")
    private String channelId;

    @Schema(description = "通道名称 (支持模糊搜索)", example = "1号枪机")
    private String name;

    @Schema(description = "通道状态 (0: 离线, 1: 在线)", example = "1")
    private Integer status;

    @Schema(description = "云台类型 (0:无, 1:球机, 2:半球, 3:固定枪机, 4:遥控枪机)", example = "1")
    private Integer ptzType;

    @Schema(description = "数据源类型 (1:GB28181, 2:推流, 3:代理, 4:ONVIF, 5:海康, 6:大华, 7:萤石, 200:JT1078)", example = "1")
    private Integer dataType;
}
