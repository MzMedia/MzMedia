package cn.iocoder.yudao.module.media.service.listener;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.event.IotMediaLinkageEvent;
import cn.iocoder.yudao.module.media.gb28181.bean.Device;
import cn.iocoder.yudao.module.media.gb28181.service.IDeviceService;
import cn.iocoder.yudao.module.media.gb28181.transmit.cmd.ISIPCommander;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 视频联动监听器
 *
 * 监听 IoT 场景与规则引擎派发的 {@link IotMediaLinkageEvent}，
 * 异步下发国标信令：
 * 1. ptz_preset: 云台球机转动到指定预置位 (0x81 指令码 129)
 * 2. record_start: 触发设备端/平台端国标录像
 * 3. record_stop: 停止录像
 */
@Component
@Slf4j
public class MediaLinkageEventListener {

    @Autowired
    private IDeviceService deviceService;

    @Autowired
    private ISIPCommander commander;

    @Async
    @EventListener
    public void onMediaLinkageEvent(IotMediaLinkageEvent event) {
        log.info("[onMediaLinkageEvent] 收到视频监控联动事件: deviceId={}, channelId={}, action={}, presetIndex={}, source={}",
                event.getDeviceId(), event.getChannelId(), event.getAction(), event.getPresetIndex(), event.getSource());

        if (StrUtil.isBlank(event.getDeviceId())) {
            log.warn("[onMediaLinkageEvent] 视频联动事件缺失 deviceId，忽略");
            return;
        }

        try {
            Device device = deviceService.getDeviceByDeviceId(event.getDeviceId());
            if (device == null) {
                log.warn("[onMediaLinkageEvent] 未查询到对应国标视频设备: {}", event.getDeviceId());
                return;
            }

            if (!device.isOnLine()) {
                log.warn("[onMediaLinkageEvent] 国标视频设备当前离线: {}", event.getDeviceId());
                return;
            }

            String channelId = StrUtil.isNotBlank(event.getChannelId()) ? event.getChannelId() : event.getDeviceId();
            String action = event.getAction();

            if ("ptz_preset".equalsIgnoreCase(action)) {
                int presetIndex = event.getPresetIndex() != null ? event.getPresetIndex() : 1;
                log.info("[onMediaLinkageEvent] 执行球机转预置位: deviceId={}, channelId={}, presetIndex={}",
                        device.getDeviceId(), channelId, presetIndex);
                // 129 (0x81) 为 GB/T 28181 预置位调用指令
                commander.frontEndCmd(device, channelId, 129, 0, presetIndex, 0);
            } else if ("record_start".equalsIgnoreCase(action)) {
                log.info("[onMediaLinkageEvent] 执行国标录像启动: deviceId={}, channelId={}", device.getDeviceId(), channelId);
                commander.recordCmd(device, channelId, "Record", null);
            } else if ("record_stop".equalsIgnoreCase(action)) {
                log.info("[onMediaLinkageEvent] 执行国标录像停止: deviceId={}, channelId={}", device.getDeviceId(), channelId);
                commander.recordCmd(device, channelId, "StopRecord", null);
            } else {
                log.warn("[onMediaLinkageEvent] 未知联动动作: {}", action);
            }
        } catch (Exception e) {
            log.error("[onMediaLinkageEvent] 处理视频监控联动异常", e);
        }
    }
}
