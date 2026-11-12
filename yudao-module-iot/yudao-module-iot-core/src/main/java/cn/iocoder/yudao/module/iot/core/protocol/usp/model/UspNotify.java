package cn.iocoder.yudao.module.iot.core.protocol.usp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * TR-369 USP Notify 主动通知事件
 *
 * 通知类型包括：
 * - ValueChange: 参数值变化通知
 * - Boot: 设备开机
 * - Periodic: 周期性上报
 * - Event: 自定义事件通知
 * - OperationComplete: 异步操作完成
 * - OnBoardRequest: 设备入网请求
 *
 * @author 芋道源码
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UspNotify {

    /**
     * 订阅编号
     */
    private String subscriptionId;

    /**
     * 是否要求 Controller 回复 NotifyResp
     */
    @Builder.Default
    private Boolean sendResp = false;

    /**
     * 通知类型 (ValueChange / Boot / Periodic / Event / OperationComplete / OnBoardRequest)
     */
    private String type;

    /**
     * 事件或属性关联的对象路径
     */
    private String objPath;

    /**
     * 事件名称 (当 type == "Event" 时有效)
     */
    private String eventName;

    /**
     * 通知附带的键值对参数
     */
    @Builder.Default
    private Map<String, String> params = new HashMap<>();

}
