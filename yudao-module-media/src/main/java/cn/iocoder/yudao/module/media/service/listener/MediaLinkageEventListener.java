package cn.iocoder.yudao.module.media.service.listener;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.event.IotMediaLinkageEvent;
import cn.iocoder.yudao.module.media.dahua.service.IDahuaService;
import cn.iocoder.yudao.module.media.ezviz.service.IEzvizService;
import cn.iocoder.yudao.module.media.gb28181.bean.Device;
import cn.iocoder.yudao.module.media.gb28181.service.IDeviceService;
import cn.iocoder.yudao.module.media.gb28181.transmit.cmd.ISIPCommander;
import cn.iocoder.yudao.module.media.hikvision.service.IHikvisionService;
import cn.iocoder.yudao.module.media.jt1078.service.IJT1078Service;
import cn.iocoder.yudao.module.media.onvif.service.IOnvifService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 视频联动监听器
 *
 * 监听 IoT 场景与规则引擎派发的 {@link IotMediaLinkageEvent}，
 * 异步下发全协议控制信令：
 * 1. GB28181 国标信令
 * 2. ONVIF / 海康威视 / 大华 / 萤石云 / JT1078 车载协议
 */
@Component
@Slf4j
public class MediaLinkageEventListener {

    @Autowired
    private IDeviceService deviceService;

    @Autowired
    private ISIPCommander commander;

    @Autowired
    private IOnvifService onvifService;

    @Autowired
    private IHikvisionService hikService;

    @Autowired
    private IDahuaService dahuaService;

    @Autowired
    private IEzvizService ezvizService;

    @Autowired
    private IJT1078Service jt1078Service;

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
            int presetIndex = event.getPresetIndex() != null ? event.getPresetIndex() : 1;
            String deviceIdStr = event.getDeviceId();

            // 1. 判断是否为其他私有/标准化协议前缀
            if (deviceIdStr.startsWith("onvif_")) {
                int devId = Integer.parseInt(deviceIdStr.replace("onvif_", ""));
                log.info("[onMediaLinkageEvent] 驱动 ONVIF 设备预置位: {}", devId);
                onvifService.presetControl(devId, 1, String.valueOf(presetIndex));
                return;
            } else if (deviceIdStr.startsWith("hik_")) {
                int devId = Integer.parseInt(deviceIdStr.replace("hik_", ""));
                log.info("[onMediaLinkageEvent] 驱动海康威视设备预置位: {}", devId);
                hikService.presetControl(devId, 1, presetIndex);
                return;
            } else if (deviceIdStr.startsWith("dh_") || deviceIdStr.startsWith("dahua_")) {
                int devId = Integer.parseInt(deviceIdStr.replaceAll("^(dh_|dahua_)", ""));
                log.info("[onMediaLinkageEvent] 驱动大华设备预置位: {}", devId);
                dahuaService.presetControl(devId, 1, presetIndex);
                return;
            } else if (deviceIdStr.startsWith("ezviz_")) {
                int devId = Integer.parseInt(deviceIdStr.replace("ezviz_", ""));
                log.info("[onMediaLinkageEvent] 驱动萤石云设备PTZ转向: {}", devId);
                ezvizService.ptzControl(devId, 0, 1, false);
                return;
            } else if (deviceIdStr.startsWith("jt_") || deviceIdStr.startsWith("1078_")) {
                String simNo = deviceIdStr.replaceAll("^(jt_|1078_)", "");
                log.info("[onMediaLinkageEvent] 驱动 JT1078 车载设备PTZ: {}", simNo);
                jt1078Service.ptzControl(simNo, 1, 0, 50);
                return;
            }

            // 2. 默认走 GB/T 28181 国标逻辑
            Device device = deviceService.getDeviceByDeviceId(deviceIdStr);
            if (device == null) {
                log.warn("[onMediaLinkageEvent] 未查询到对应国标视频设备: {}", deviceIdStr);
                return;
            }

            if (!device.isOnLine()) {
                log.warn("[onMediaLinkageEvent] 国标视频设备当前离线: {}", deviceIdStr);
                return;
            }

            String channelId = StrUtil.isNotBlank(event.getChannelId()) ? event.getChannelId() : device.getDeviceId();
            String action = event.getAction();

            if ("ptz_preset".equalsIgnoreCase(action)) {
                log.info("[onMediaLinkageEvent] 执行国标球机转预置位: deviceId={}, channelId={}, presetIndex={}",
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
