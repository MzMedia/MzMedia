package cn.iocoder.yudao.module.media.jt1078.service;

import cn.iocoder.yudao.module.media.jt1078.bean.JT1078Device;

import java.util.List;

public interface IJT1078Service {

    List<JT1078Device> listDevices();

    JT1078Device getDevice(int id);

    JT1078Device getDeviceBySim(String simNo);

    JT1078Device saveDevice(JT1078Device device);

    boolean deleteDevice(int id);

    String getStreamUrl(String simNo, int channelNo);

    boolean requestRealTimeStream(String simNo, int channelNo);

    boolean stopRealTimeStream(String simNo, int channelNo);

    boolean ptzControl(String simNo, int channelNo, int ptzCmd, int speed);
}
