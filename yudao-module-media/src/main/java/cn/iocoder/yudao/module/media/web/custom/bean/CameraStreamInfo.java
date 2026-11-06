package cn.iocoder.yudao.module.media.web.custom.bean;

import cn.iocoder.yudao.module.media.common.StreamInfo;
import cn.iocoder.yudao.module.media.gb28181.bean.CommonGBChannel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CameraStreamInfo {


    private CommonGBChannel channel;


    private StreamInfo streamInfo;

    public CameraStreamInfo(CommonGBChannel channel, StreamInfo streamInfo) {
        this.channel = channel;
        this.streamInfo = streamInfo;
    }
}
