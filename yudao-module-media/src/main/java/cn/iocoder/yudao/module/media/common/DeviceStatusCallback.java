package cn.iocoder.yudao.module.media.common;

import cn.iocoder.yudao.module.media.gb28181.bean.SipTransactionInfo;

public interface DeviceStatusCallback {
    public void run(String deviceId, SipTransactionInfo transactionInfo);
}
