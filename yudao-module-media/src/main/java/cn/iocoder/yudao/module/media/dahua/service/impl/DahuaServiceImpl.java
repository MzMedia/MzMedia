package cn.iocoder.yudao.module.media.dahua.service.impl;

import cn.iocoder.yudao.module.media.dahua.bean.DahuaDevice;
import cn.iocoder.yudao.module.media.dahua.client.DahuaRpcClient;
import cn.iocoder.yudao.module.media.dahua.service.IDahuaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class DahuaServiceImpl implements IDahuaService {

    @Autowired
    private DahuaRpcClient rpcClient;

    private final Map<Integer, DahuaDevice> deviceCache = new ConcurrentHashMap<>();
    private final AtomicInteger idGenerator = new AtomicInteger(3000);

    @Override
    public List<DahuaDevice> listDevices() {
        return new ArrayList<>(deviceCache.values());
    }

    @Override
    public DahuaDevice getDevice(int id) {
        return deviceCache.get(id);
    }

    @Override
    public DahuaDevice saveDevice(DahuaDevice device) {
        if (device.getId() == null || device.getId() <= 0) {
            device.setId(idGenerator.incrementAndGet());
            device.setCreateTime(LocalDateTime.now());
        }
        device.setUpdateTime(LocalDateTime.now());
        if (device.getChannelCount() == null || device.getChannelCount() <= 0) {
            device.setChannelCount(1);
        }

        try {
            rpcClient.fetchSystemInfo(device);
        } catch (Exception e) {
            log.warn("[大华] 获取系统信息异常: {}", e.getMessage());
        }

        if (device.getDefaultRtspUrl() == null || device.getDefaultRtspUrl().isEmpty()) {
            device.setDefaultRtspUrl(rpcClient.getRtspStreamUrl(device, 1, 0));
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
        DahuaDevice device = deviceCache.get(deviceId);
        if (device == null) return null;
        return rpcClient.getRtspStreamUrl(device, channelNo > 0 ? channelNo : 1, 0);
    }

    @Override
    public boolean ptzControl(int deviceId, int channelNo, String action, String code, int speed) {
        DahuaDevice device = deviceCache.get(deviceId);
        if (device == null) return false;
        int ch = channelNo > 0 ? channelNo : 1;
        return rpcClient.ptzAction(device, ch, action != null ? action : "start", code != null ? code : "Up", speed);
    }

    @Override
    public boolean presetControl(int deviceId, int channelNo, int presetIndex) {
        DahuaDevice device = deviceCache.get(deviceId);
        if (device == null) return false;
        int ch = channelNo > 0 ? channelNo : 1;
        return rpcClient.gotoPreset(device, ch, presetIndex);
    }

    @Override
    public byte[] capturePicture(int deviceId, int channelNo) {
        DahuaDevice device = deviceCache.get(deviceId);
        if (device == null) return null;
        int ch = channelNo > 0 ? channelNo : 1;
        return rpcClient.capturePicture(device, ch);
    }
}
