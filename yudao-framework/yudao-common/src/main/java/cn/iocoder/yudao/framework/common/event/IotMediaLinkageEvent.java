package cn.iocoder.yudao.framework.common.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 物联网设备与视频监控联动事件
 *
 * 当 IoT 规则引擎（如烟感报警、红外感应、越界触发）匹配执行时，
 * 发布此联动事件，由视频监控模块监听并驱动国标球机预置位转向、抓拍或录像。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IotMediaLinkageEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 租户编号
     */
    private Long tenantId;

    /**
     * 联动动作类型：
     * ptz_preset: 调用云台预置位
     * record_start: 启动国标录像
     * record_stop: 停止国标录像
     */
    private String action;

    /**
     * 目标国标视频设备国标编号
     */
    private String deviceId;

    /**
     * 目标视频通道国标编号
     */
    private String channelId;

    /**
     * 预置位编号 (1-255)
     */
    private Integer presetIndex;

    /**
     * 触发源说明（如：规则名称、告警详情）
     */
    private String source;

}
