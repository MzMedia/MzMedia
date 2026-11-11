package cn.iocoder.yudao.module.media.onvif.service;

import cn.iocoder.yudao.module.media.onvif.bean.OnvifDevice;

import java.util.List;

public interface IOnvifService {

    /**
     * 局域网 WS-Discovery 探测 ONVIF 设备
     */
    List<OnvifDevice> discover(int timeoutMs);

    /**
     * 查询所有已配置的 ONVIF 设备
     */
    List<OnvifDevice> listDevices();

    /**
     * 获取指定 ID 的 ONVIF 设备
     */
    OnvifDevice getDevice(int id);

    /**
     * 保存或更新 ONVIF 设备配置并自动拉取通道信息与 RTSP
     */
    OnvifDevice saveDevice(OnvifDevice device);

    /**
     * 删除 ONVIF 设备
     */
    boolean deleteDevice(int id);

    /**
     * 获取点播 RTSP 地址
     */
    String getPlayStreamUrl(int deviceId);

    /**
     * 云台方向控制
     */
    boolean ptzControl(int deviceId, Integer pan, Integer tilt, Integer zoom, Double speed);

    /**
     * 预置位控制
     */
    boolean presetControl(int deviceId, Integer code, String presetToken);
}
