package cn.iocoder.yudao.module.media.controller.admin.channel.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 视频通道 Response VO
 *
 * @author MzMedia Architect
 */
@Schema(description = "管理后台 - 视频通道详情 Response VO")
@Data
public class VideoChannelRespVO {

    @Schema(description = "自增主键 ID", example = "1001")
    private Long id;

    @Schema(description = "通道国标编码 (20位)", example = "34020000001320000001")
    private String channelId;

    @Schema(description = "所属设备国标编号", example = "34020000001110000001")
    private String deviceId;

    @Schema(description = "通道名称", example = "前门高清枪机")
    private String name;

    @Schema(description = "自定义通道名称", example = "主入口通道")
    private String customName;

    @Schema(description = "厂商", example = "Dahua")
    private String manufacturer;

    @Schema(description = "型号", example = "DH-IPC-HFW")
    private String model;

    @Schema(description = "安装地址", example = "科技园区A栋入口")
    private String address;

    @Schema(description = "行政区划代码", example = "340200")
    private String civilCode;

    @Schema(description = "通道状态 (0:离线, 1:在线, 2:推流中)", example = "1")
    private Integer status;

    @Schema(description = "云台类型 (0:无, 1:球机, 2:半球, 3:固定枪机, 4:遥控枪机)", example = "1")
    private Integer ptzType;

    @Schema(description = "数据源类型 (1:GB28181, 2:推流, 3:代理, 4:ONVIF, 5:海康, 6:大华, 7:萤石, 200:JT1078)", example = "1")
    private Integer dataType;

    @Schema(description = "是否有音频 (0:无, 1:有)", example = "1")
    private Integer hasAudio;

    @Schema(description = "经度", example = "120.123456")
    private Double longitude;

    @Schema(description = "纬度", example = "30.123456")
    private Double latitude;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
