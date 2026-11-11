package cn.iocoder.yudao.module.media.jt1078.bean;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * JT1078 部标车载音视频终端实体
 *
 * @author MzMedia
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "JT1078车载设备信息")
public class JT1078Device {

    @Schema(description = "主键ID")
    private Integer id;

    @Schema(description = "终端SIM卡号(BCD格式，如013800138000)")
    private String simNo;

    @Schema(description = "车牌号码")
    private String plateNo;

    @Schema(description = "协议标准版本(2013/2019)")
    private String protocolVersion;

    @Schema(description = "通道数量")
    private Integer channelCount;

    @Schema(description = "音频编码(G726/ADPCM/AAC)")
    private String audioCodec;

    @Schema(description = "视频编码(H264/H265)")
    private String videoCodec;

    @Schema(description = "在线状态(0:离线, 1:在线)")
    private Integer status;

    @Schema(description = "关联流媒体服务节点")
    private String mediaServerId;

    @Schema(description = "最后心跳时间")
    private LocalDateTime lastHeartbeat;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
