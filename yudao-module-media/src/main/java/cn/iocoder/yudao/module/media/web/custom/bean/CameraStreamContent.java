package cn.iocoder.yudao.module.media.web.custom.bean;

import cn.iocoder.yudao.module.media.common.StreamInfo;
import cn.iocoder.yudao.module.media.vmanager.bean.StreamContent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CameraStreamContent extends StreamContent {

    public CameraStreamContent(StreamInfo streamInfo) {
        super(streamInfo);
    }

    private String name;

    // 0不可动，1可动
    private Integer controlType;


}
