package cn.iocoder.yudao.module.media.hikvision.bean;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 海康威视 ISAPI 设备配置
 *
 * @author MzMedia
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "海康威视设备信息")
public class HikDevice {

    @Schema(description = "设备主键ID")
    private Integer id;

    @Schema(description = "设备名称")
    private String name;

    @Schema(description = "设备IP地址")
    private String ip;

    @Schema(description = "HTTP / ISAPI 端口")
    private Integer port;

    @Schema(description = "认证用户名")
    private String username;

    @Schema(description = "认证密码")
    private String password;

    @Schema(description = "设备序列号")
    private String serialNumber;

    @Schema(description = "设备型号")
    private String model;

    @Schema(description = "固件版本")
    private String firmware;

    @Schema(description = "物理视频通道数")
    private Integer channelCount;

    @Schema(description = "在线状态(0:离线, 1:在线)")
    private Integer status;

    @Schema(description = "关联流媒体服务节点")
    private String mediaServerId;

    @Schema(description = "默认RTSP流地址")
    private String defaultRtspUrl;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
