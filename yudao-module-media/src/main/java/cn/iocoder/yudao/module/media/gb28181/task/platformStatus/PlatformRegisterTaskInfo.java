package cn.iocoder.yudao.module.media.gb28181.task.platformStatus;

import cn.iocoder.yudao.module.media.gb28181.bean.SipTransactionInfo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 平台注册任务可序列化的信息
 */
@Slf4j
@Data
public class PlatformRegisterTaskInfo{

    private String platformServerId;

    private SipTransactionInfo sipTransactionInfo;

    /**
     * 过期时间，单位： 毫秒
     */
    private long expireTime;
}
