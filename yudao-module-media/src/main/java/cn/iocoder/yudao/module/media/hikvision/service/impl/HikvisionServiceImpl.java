package cn.iocoder.yudao.module.media.hikvision.service.impl;

import cn.iocoder.yudao.module.media.hikvision.bean.HikDevice;
import cn.iocoder.yudao.module.media.hikvision.client.HikIsapiClient;
import cn.iocoder.yudao.module.media.hikvision.service.IHikvisionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class HikvisionServiceImpl implements IHikvisionService {

    @Autowired
    private HikIsapiClient isapiClient;

    private final Map<Integer, HikDevice> deviceCache = new ConcurrentHashMap<>();
    private final AtomicInteger idGenerator = new AtomicInteger(2000);

    @Override
    public List<HikDevice> listDevices() {
        return new ArrayList<>(deviceCache.values());
    }

    @Override
    public HikDevice getDevice(int id) {
        return deviceCache.get(id);
    }

    @Override
    public HikDevice saveDevice(HikDevice device) {
        if (device.getId() == null || device.getId() <= 0) {
            device.setId(idGenerator.incrementAndGet());
            device.setCreateTime(LocalDateTime.now());
        }
        device.setUpdateTime(LocalDateTime.now());
        if (device.getChannelCount() == null || device.getChannelCount() <= 0) {
            device.setChannelCount(1);
        }

        // 尝试自动读取设备信息
        try {
            isapiClient.fetchDeviceInfo(device);
        } catch (Exception e) {
            log.warn("[海康] 自动拉取设备信息异常: {}", e.getMessage());
        }

        if (device.getDefaultRtspUrl() == null || device.getDefaultRtspUrl().isEmpty()) {
            device.setDefaultRtspUrl(isapiClient.getRtspStreamUrl(device, 1, 0));
        }

        deviceCache.put(device.getId(), device);
        return device;
    }

    @Override
    public boolean deleteDevice(int id) {
        return deviceCache.remove(id) != null;
    }

    @Override
    public String getStreamUrl(int deviceId, int channelNo) {
        HikDevice device = deviceCache.get(deviceId);
        if (device == null) return null;
        return isapiClient.getRtspStreamUrl(device, channelNo > 0 ? channelNo : 1, 0);
    }

    @Override
    public boolean ptzControl(int deviceId, int channelNo, Integer pan, Integer tilt, Integer zoom, Integer speed) {
        HikDevice device = deviceCache.get(deviceId);
        if (device == null) return false;
        int ch = channelNo > 0 ? channelNo : 1;
        int sp = speed != null ? speed : 50;

        int panVal = pan != null ? pan : 0;
        int tiltVal = tilt != null ? tilt : 0;
        int zoomVal = zoom != null ? zoom : 0;

        return isapiClient.ptzControl(device, ch, panVal, tiltVal, zoomVal, sp);
    }

    @Override
    public boolean presetControl(int deviceId, int channelNo, Integer presetIndex) {
        HikDevice device = deviceCache.get(deviceId);
        if (device == null) return false;
        int ch = channelNo > 0 ? channelNo : 1;
        return isapiClient.gotoPreset(device, ch, presetIndex != null ? presetIndex : 1);
    }

    @Override
    public byte[] capturePicture(int deviceId, int channelNo) {
        HikDevice device = deviceCache.get(deviceId);
        if (device == null) return null;
        int ch = channelNo > 0 ? channelNo : 1;
        return isapiClient.capturePicture(device, ch);
    }
}
