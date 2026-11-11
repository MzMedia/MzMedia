package cn.iocoder.yudao.module.media.onvif.bean;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * ONVIF 设备配置与状态
 *
 * @author MzMedia
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "ONVIF 设备信息")
public class OnvifDevice {

    @Schema(description = "主键ID")
    private Integer id;

    @Schema(description = "设备名称")
    private String name;

    @Schema(description = "设备IP地址")
    private String ip;

    @Schema(description = "ONVIF服务端口")
    private Integer port;

    @Schema(description = "认证用户名")
    private String username;

    @Schema(description = "认证密码")
    private String password;

    @Schema(description = "设备生产厂商")
    private String manufacturer;

    @Schema(description = "设备型号")
    private String model;

    @Schema(description = "设备序列号")
    private String serialNumber;

    @Schema(description = "固件版本")
    private String firmware;

    @Schema(description = "当前ProfileToken")
    private String profileToken;

    @Schema(description = "PTZ节点Token")
    private String ptzNodeToken;

    @Schema(description = "RTSP流地址")
    private String rtspUrl;

    @Schema(description = "在线状态(0:离线, 1:在线)")
    private Integer status;

    @Schema(description = "是否自动通过ZLM拉流代理")
    private Boolean autoPull;

    @Schema(description = "关联流媒体服务节点ID")
    private String mediaServerId;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
