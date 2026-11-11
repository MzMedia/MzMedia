package cn.iocoder.yudao.module.media.dahua.service;

import cn.iocoder.yudao.module.media.dahua.bean.DahuaDevice;

import java.util.List;

public interface IDahuaService {

    List<DahuaDevice> listDevices();

    DahuaDevice getDevice(int id);

    DahuaDevice saveDevice(DahuaDevice device);

    boolean deleteDevice(int id);

    String getStreamUrl(int deviceId, int channelNo);

    boolean ptzControl(int deviceId, int channelNo, String action, String code, int speed);

    boolean presetControl(int deviceId, int channelNo, int presetIndex);

    byte[] capturePicture(int deviceId, int channelNo);
}
