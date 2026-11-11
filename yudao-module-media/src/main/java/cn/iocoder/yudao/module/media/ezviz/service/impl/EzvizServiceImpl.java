package cn.iocoder.yudao.module.media.ezviz.service.impl;

import cn.iocoder.yudao.module.media.ezviz.bean.EzvizDevice;
import cn.iocoder.yudao.module.media.ezviz.client.EzvizApiClient;
import cn.iocoder.yudao.module.media.ezviz.service.IEzvizService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class EzvizServiceImpl implements IEzvizService {

    @Autowired
    private EzvizApiClient apiClient;

    private final Map<Integer, EzvizDevice> deviceCache = new ConcurrentHashMap<>();
    private final AtomicInteger idGenerator = new AtomicInteger(4000);

    @Override
    public List<EzvizDevice> listDevices() {
        return new ArrayList<>(deviceCache.values());
    }

    @Override
    public EzvizDevice getDevice(int id) {
        return deviceCache.get(id);
    }

    @Override
    public EzvizDevice saveDevice(EzvizDevice device) {
        if (device.getId() == null || device.getId() <= 0) {
            device.setId(idGenerator.incrementAndGet());
            device.setCreateTime(LocalDateTime.now());
        }
        device.setUpdateTime(LocalDateTime.now());
        if (device.getChannelNo() == null || device.getChannelNo() <= 0) {
            device.setChannelNo(1);
        }

        // 尝试获取直播地址
        try {
            String liveUrl = apiClient.getLiveAddress(device.getAppKey(), device.getAppSecret(), device.getDeviceSerial(), device.getChannelNo());
            if (liveUrl != null) {
                device.setLiveUrl(liveUrl);
                device.setStatus(1);
            }
        } catch (Exception e) {
            log.warn("[萤石云] 获取设备直播流失败: {}", e.getMessage());
        }

        deviceCache.put(device.getId(), device);
        return device;
    }

    @Override
    public boolean deleteDevice(int id) {
        return deviceCache.remove(id) != null;
    }

    @Override
    public String getLiveUrl(int deviceId) {
        EzvizDevice device = deviceCache.get(deviceId);
        if (device == null) return null;
        if (device.getLiveUrl() != null && !device.getLiveUrl().isEmpty()) {
            return device.getLiveUrl();
        }
        String url = apiClient.getLiveAddress(device.getAppKey(), device.getAppSecret(), device.getDeviceSerial(), device.getChannelNo());
        device.setLiveUrl(url);
        return url;
    }

    @Override
    public boolean ptzControl(int deviceId, Integer direction, Integer speed, boolean stop) {
        EzvizDevice device = deviceCache.get(deviceId);
        if (device == null) return false;
        int dir = direction != null ? direction : 0;
        int sp = speed != null ? speed : 1;

        if (stop) {
            return apiClient.ptzStop(device.getAppKey(), device.getAppSecret(), device.getDeviceSerial(), device.getChannelNo(), dir);
        } else {
            return apiClient.ptzStart(device.getAppKey(), device.getAppSecret(), device.getDeviceSerial(), device.getChannelNo(), dir, sp);
        }
    }

    @Override
    public String capturePicture(int deviceId) {
        EzvizDevice device = deviceCache.get(deviceId);
        if (device == null) return null;
        return apiClient.capturePictureUrl(device.getAppKey(), device.getAppSecret(), device.getDeviceSerial(), device.getChannelNo());
    }
}
