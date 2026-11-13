package cn.iocoder.yudao.module.media.controller.admin.device.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 视频设备 Response VO
 *
 * @author MzMedia Architect
 */
@Schema(description = "管理后台 - 视频设备详情 Response VO")
@Data
public class VideoDeviceRespVO {

    @Schema(description = "自增主键 ID", example = "101")
    private Long id;

    @Schema(description = "设备国标编码 (20位)", example = "34020000001110000001")
    private String deviceId;

    @Schema(description = "设备名称", example = "车间入口枪机")
    private String name;

    @Schema(description = "自定义别名", example = "一号车间摄像机")
    private String customName;

    @Schema(description = "设备生产厂商", example = "Hikvision")
    private String manufacturer;

    @Schema(description = "设备型号", example = "DS-2CD2T47WD-L")
    private String model;

    @Schema(description = "固件版本", example = "V5.7.3")
    private String firmware;

    @Schema(description = "信令传输模式 (UDP/TCP)", example = "UDP")
    private String transport;

    @Schema(description = "流传输模式", example = "UDP")
    private String streamMode;

    @Schema(description = "在线状态 (0: 离线, 1: 在线)", example = "1")
    private Integer onLine;

    @Schema(description = "设备 IP", example = "192.168.1.100")
    private String ip;

    @Schema(description = "设备 SIP 端口", example = "5060")
    private Integer port;

    @Schema(description = "最近注册时间", example = "2026-11-13 12:00:00")
    private String registerTime;

    @Schema(description = "最近心跳保活时间", example = "2026-11-13 12:30:00")
    private String keepaliveTime;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
