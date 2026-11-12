package cn.iocoder.yudao.module.iot.core.protocol.cwmp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TR-069 事件结构 (EventStruct)
 *
 * 常见事件码：
 * 0 BOOTSTRAP: 首次接入/出厂后首次连接
 * 1 BOOT: 设备重新开机
 * 2 PERIODIC: 周期性上报
 * 4 VALUE CHANGE: 诊断或关键参数值改变
 * 6 CONNECTION REQUEST: 收到 ACS 的 Connection Request 后发起的连接
 * 7 TRANSFER COMPLETE: 文件传输/固件升级完成
 * 8 DIAGNOSTICS COMPLETE: 诊断完成
 * M Reboot: 管理员触发的重启
 * M ScheduleInform: 计划的 Inform
 *
 * @author 芋道源码
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CwmpEvent {

    /**
     * 事件代码
     */
    private String eventCode;

    /**
     * 命令标识 Key
     */
    private String commandKey;

}
