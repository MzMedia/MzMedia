package cn.iocoder.yudao.module.media.vmanager.bean;

import cn.iocoder.yudao.module.media.common.VersionPo;
import cn.iocoder.yudao.module.media.conf.SipConfig;
import cn.iocoder.yudao.module.media.conf.UserSetting;
import cn.iocoder.yudao.module.media.jt1078.config.JT1078Config;
import lombok.Data;

@Data
public class SystemConfigInfo {

    private int serverPort;
    private SipConfig sip;
    private UserSetting addOn;
    private VersionPo version;
    private JT1078Config jt1078Config;

}

