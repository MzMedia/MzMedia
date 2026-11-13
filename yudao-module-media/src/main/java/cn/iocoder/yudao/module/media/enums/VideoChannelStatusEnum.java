package cn.iocoder.yudao.module.media.enums;

import cn.hutool.core.util.ObjUtil;
import cn.iocoder.yudao.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 视频通道状态枚举
 *
 * @author MzMedia Architect
 */
@Getter
@AllArgsConstructor
public enum VideoChannelStatusEnum implements ArrayValuable<Integer> {

    OFFLINE(0, "离线"),
    ONLINE(1, "在线"),
    STREAMING(2, "正在推流中");

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(VideoChannelStatusEnum::getCode).toArray(Integer[]::new);

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
        return ObjUtil.equal(ONLINE.code, status) || ObjUtil.equal(STREAMING.code, status);
    }
}
