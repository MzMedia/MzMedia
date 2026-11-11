package cn.iocoder.yudao.module.media.ezviz.service;

import cn.iocoder.yudao.module.media.ezviz.bean.EzvizDevice;

import java.util.List;

public interface IEzvizService {

    List<EzvizDevice> listDevices();

    EzvizDevice getDevice(int id);

    EzvizDevice saveDevice(EzvizDevice device);

    boolean deleteDevice(int id);

    String getLiveUrl(int deviceId);

    boolean ptzControl(int deviceId, Integer direction, Integer speed, boolean stop);

    String capturePicture(int deviceId);
}
