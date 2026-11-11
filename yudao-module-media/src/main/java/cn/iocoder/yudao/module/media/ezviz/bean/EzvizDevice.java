package cn.iocoder.yudao.module.media.ezviz.bean;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 萤石云设备配置
 *
 * @author MzMedia
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "萤石云设备信息")
public class EzvizDevice {

    @Schema(description = "设备主键ID")
    private Integer id;

    @Schema(description = "设备名称")
    private String name;

    @Schema(description = "萤石开放平台 AppKey")
    private String appKey;

    @Schema(description = "萤石开放平台 AppSecret")
    private String appSecret;

    @Schema(description = "设备序列号")
    private String deviceSerial;

    @Schema(description = "通道编号")
    private Integer channelNo;

    @Schema(description = "设备验证码(机身标签6位大写字母)")
    private String verifyCode;

    @Schema(description = "实时播放地址(HLS/HTTP-FLV)")
    private String liveUrl;

    @Schema(description = "在线状态(0:离线, 1:在线)")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
