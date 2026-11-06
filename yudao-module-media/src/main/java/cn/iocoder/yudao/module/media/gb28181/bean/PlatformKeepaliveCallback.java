package cn.iocoder.yudao.module.media.gb28181.bean;

public interface PlatformKeepaliveCallback {
    public void run(String platformServerGbId, int failCount);
}
