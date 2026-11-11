package cn.iocoder.yudao.module.media.jt1078.service.impl;

import cn.iocoder.yudao.module.media.jt1078.bean.JT1078Device;
import cn.iocoder.yudao.module.media.jt1078.service.IJT1078Service;
import cn.iocoder.yudao.module.media.jt1078.session.JT1078SessionManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class JT1078ServiceImpl implements IJT1078Service {

    @Autowired
    private JT1078SessionManager sessionManager;

    private final Map<Integer, JT1078Device> deviceMap = new ConcurrentHashMap<>();
    private final Map<String, Integer> simIdMap = new ConcurrentHashMap<>();
    private final AtomicInteger idGen = new AtomicInteger(5000);

    @Override
    public List<JT1078Device> listDevices() {
        List<JT1078Device> list = new ArrayList<>(deviceMap.values());
        for (JT1078Device d : list) {
            d.setStatus(sessionManager.isOnline(d.getSimNo()) ? 1 : 0);
        }
        return list;
    }

    @Override
    public JT1078Device getDevice(int id) {
        JT1078Device d = deviceMap.get(id);
        if (d != null) {
            d.setStatus(sessionManager.isOnline(d.getSimNo()) ? 1 : 0);
        }
        return d;
    }

    @Override
    public JT1078Device getDeviceBySim(String simNo) {
        Integer id = simIdMap.get(simNo);
        return id != null ? getDevice(id) : null;
    }

    @Override
    public JT1078Device saveDevice(JT1078Device device) {
        if (device.getId() == null || device.getId() <= 0) {
            device.setId(idGen.incrementAndGet());
            device.setCreateTime(LocalDateTime.now());
        }
        device.setUpdateTime(LocalDateTime.now());
        if (device.getChannelCount() == null || device.getChannelCount() <= 0) {
            device.setChannelCount(4);
        }

        deviceMap.put(device.getId(), device);
        simIdMap.put(device.getSimNo(), device.getId());
        return device;
    }

    @Override
    public boolean deleteDevice(int id) {
        JT1078Device d = deviceMap.remove(id);
        if (d != null) {
            simIdMap.remove(d.getSimNo());
            return true;
        }
        return false;
    }

    @Override
    public String getStreamUrl(String simNo, int channelNo) {
        // ZLMediaKit 中对应的 JT1078 RTP 流地址 (rtp/sim_channel)
        return String.format("rtp/%s_%d", simNo, channelNo > 0 ? channelNo : 1);
    }

    @Override
    public boolean requestRealTimeStream(String simNo, int channelNo) {
        log.info("[JT1078] 模拟下发 0x9101 实时音视频传输请求: SIM={}, Channel={}", simNo, channelNo);
        return sessionManager.isOnline(simNo);
    }

    @Override
    public boolean stopRealTimeStream(String simNo, int channelNo) {
        log.info("[JT1078] 模拟下发 0x9102 停止音视频传输控制: SIM={}, Channel={}", simNo, channelNo);
        return true;
    }

    @Override
    public boolean ptzControl(String simNo, int channelNo, int ptzCmd, int speed) {
        log.info("[JT1078] 模拟下发 0x9301 云台控制命令: SIM={}, Channel={}, Cmd={}, Speed={}", simNo, channelNo, ptzCmd, speed);
        return sessionManager.isOnline(simNo);
    }
}
