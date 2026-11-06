package cn.iocoder.yudao.module.media.media.zlm.dto;

import cn.iocoder.yudao.module.media.gb28181.bean.SendRtpInfo;

import java.text.ParseException;

/**
 * @author lin
 */
public interface ChannelOnlineEvent {

    void run(SendRtpInfo sendRtpItem) throws ParseException;
}
