package cn.iocoder.yudao.module.media.onvif.service.impl;

import cn.iocoder.yudao.module.media.onvif.bean.OnvifDevice;
import cn.iocoder.yudao.module.media.onvif.client.OnvifSoapClient;
import cn.iocoder.yudao.module.media.onvif.service.IOnvifService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class OnvifServiceImpl implements IOnvifService {

    @Autowired
    private OnvifSoapClient soapClient;

    private final Map<Integer, OnvifDevice> deviceCache = new ConcurrentHashMap<>();
    private final AtomicInteger idGenerator = new AtomicInteger(1000);

    @Override
    public List<OnvifDevice> discover(int timeoutMs) {
        return soapClient.discoverDevices(timeoutMs);
    }

    @Override
    public List<OnvifDevice> listDevices() {
        return new ArrayList<>(deviceCache.values());
    }

    @Override
    public OnvifDevice getDevice(int id) {
        return deviceCache.get(id);
    }

    @Override
    public OnvifDevice saveDevice(OnvifDevice device) {
        if (device.getId() == null || device.getId() <= 0) {
            device.setId(idGenerator.incrementAndGet());
            device.setCreateTime(LocalDateTime.now());
        }
        device.setUpdateTime(LocalDateTime.now());

        // 尝试自动获取设备硬件信息及 Profile
        try {
            soapClient.fetchDeviceInfo(device);
            List<String> profiles = soapClient.getProfiles(device);
            if (!profiles.isEmpty()) {
                device.setProfileToken(profiles.get(0));
            }
            String rtsp = soapClient.getStreamUri(device, device.getProfileToken());
            device.setRtspUrl(rtsp);
            device.setStatus(1);
        } catch (Exception e) {
            log.warn("[ONVIF] 获取设备媒体信息失败: {}", e.getMessage());
            if (device.getRtspUrl() == null) {
                device.setRtspUrl("rtsp://" + device.getUsername() + ":" + device.getPassword() + "@" + device.getIp() + ":554/live/ch0");
            }
        }

        deviceCache.put(device.getId(), device);
        return device;
    }

    @Override
    public boolean deleteDevice(int id) {
        return deviceCache.remove(id) != null;
    }

    @Override
    public String getPlayStreamUrl(int deviceId) {
        OnvifDevice device = deviceCache.get(deviceId);
        if (device == null) {
            return null;
        }
        if (device.getRtspUrl() != null && !device.getRtspUrl().isEmpty()) {
            return device.getRtspUrl();
        }
        String uri = soapClient.getStreamUri(device, device.getProfileToken());
        device.setRtspUrl(uri);
        return uri;
    }

    @Override
    public boolean ptzControl(int deviceId, Integer pan, Integer tilt, Integer zoom, Double speed) {
        OnvifDevice device = deviceCache.get(deviceId);
        if (device == null) return false;

        double sp = speed != null ? speed / 100.0 : 0.5;
        if (pan == null && tilt == null && zoom == null) {
            return soapClient.ptzStop(device, device.getProfileToken());
        }

        double panVal = 0.0;
        double tiltVal = 0.0;
        double zoomVal = 0.0;

        if (pan != null) {
            panVal = pan > 0 ? sp : -sp;
        }
        if (tilt != null) {
            tiltVal = tilt > 0 ? sp : -sp;
        }
        if (zoom != null) {
            zoomVal = zoom > 0 ? sp : -sp;
        }

        return soapClient.ptzContinuousMove(device, device.getProfileToken(), panVal, tiltVal, zoomVal);
    }

    @Override
    public boolean presetControl(int deviceId, Integer code, String presetToken) {
        OnvifDevice device = deviceCache.get(deviceId);
        if (device == null) return false;
        if (code != null && code == 1) { // 1 代表调用预置位
            return soapClient.presetGoto(device, device.getProfileToken(), presetToken != null ? presetToken : "1");
        }
        return true;
    }
}
