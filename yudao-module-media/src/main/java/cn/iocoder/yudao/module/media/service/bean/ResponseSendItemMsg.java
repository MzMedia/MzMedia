package cn.iocoder.yudao.module.media.service.bean;

import cn.iocoder.yudao.module.media.gb28181.bean.SendRtpInfo;
import cn.iocoder.yudao.module.media.media.bean.MediaServer;

/**
 * redis消息：下级回复推送信息
 * @author lin
 */
public class ResponseSendItemMsg {

    private SendRtpInfo sendRtpItem;

    private MediaServer mediaServerItem;

    public SendRtpInfo getSendRtpItem() {
        return sendRtpItem;
    }

    public void setSendRtpItem(SendRtpInfo sendRtpItem) {
        this.sendRtpItem = sendRtpItem;
    }

    public MediaServer getMediaServerItem() {
        return mediaServerItem;
    }

    public void setMediaServerItem(MediaServer mediaServerItem) {
        this.mediaServerItem = mediaServerItem;
    }
}
