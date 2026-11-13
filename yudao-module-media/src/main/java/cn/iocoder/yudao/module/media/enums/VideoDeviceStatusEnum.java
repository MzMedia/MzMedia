package cn.iocoder.yudao.module.media.enums;

import cn.hutool.core.util.ObjUtil;
import cn.iocoder.yudao.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 视频设备在线状态枚举
 *
 * @author MzMedia Architect
 */
@Getter
@AllArgsConstructor
public enum VideoDeviceStatusEnum implements ArrayValuable<Integer> {

    OFFLINE(0, "离线"),
    ONLINE(1, "在线");

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(VideoDeviceStatusEnum::getCode).toArray(Integer[]::new);

    /**
     * 状态码
     */
    private final Integer code;
    /**
     * 描述
     */
    private final String name;

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

    public static boolean isOnline(Integer status) {
        return ObjUtil.equal(ONLINE.code, status);
    }
}
