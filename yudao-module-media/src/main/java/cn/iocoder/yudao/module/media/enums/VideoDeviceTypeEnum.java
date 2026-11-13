package cn.iocoder.yudao.module.media.enums;

import cn.iocoder.yudao.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 视频设备/通道类型枚举
 *
 * @author MzMedia Architect
 */
@Getter
@AllArgsConstructor
public enum VideoDeviceTypeEnum implements ArrayValuable<Integer> {

    GB28181(1, "国标GB28181设备"),
    STREAM_PUSH(2, "自主推流设备"),
    STREAM_PROXY(3, "拉流代理设备"),
    ONVIF(4, "ONVIF协议网络摄像头"),
    HIKVISION(5, "海康威视ISAPI直连"),
    DAHUA(6, "大华RPC直连"),
    EZVIZ(7, "萤石云开放平台"),
    JT1078(200, "交通部JT/T 1078车载终端");

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(VideoDeviceTypeEnum::getCode).toArray(Integer[]::new);

    private final Integer code;
    private final String name;

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

    public static VideoDeviceTypeEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (VideoDeviceTypeEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }
}
