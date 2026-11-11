package cn.iocoder.yudao.module.media.hikvision.service;

import cn.iocoder.yudao.module.media.hikvision.bean.HikDevice;

import java.util.List;

public interface IHikvisionService {

    List<HikDevice> listDevices();

    HikDevice getDevice(int id);

    HikDevice saveDevice(HikDevice device);

    boolean deleteDevice(int id);

    String getStreamUrl(int deviceId, int channelNo);

    boolean ptzControl(int deviceId, int channelNo, Integer pan, Integer tilt, Integer zoom, Integer speed);

    boolean presetControl(int deviceId, int channelNo, Integer presetIndex);

    byte[] capturePicture(int deviceId, int channelNo);
}
