package cn.iocoder.yudao.module.media.vmanager.bean;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 历史 WVP 响应类
 *
 * @deprecated 建议优先使用 {@link MzResult} 或 ruoyi-vue-pro 标准 {@link CommonResult}。
 * @param <T> 响应数据类型
 */
@Deprecated
@Schema(description = "历史统一返回结果（已废弃）")
public class WVPResult<T> extends MzResult<T> {

    public WVPResult() {
        super();
    }

    public WVPResult(int code, String msg, T data) {
        super(code, msg, data);
    }

    public static <T> WVPResult<T> success(T t, String msg) {
        return new WVPResult<>(ErrorCode.SUCCESS.getCode(), msg, t);
    }

    public static <T> WVPResult<T> success() {
        return new WVPResult<>(ErrorCode.SUCCESS.getCode(), ErrorCode.SUCCESS.getMsg(), null);
    }

    public static <T> WVPResult<T> success(T t) {
        return success(t, ErrorCode.SUCCESS.getMsg());
    }

    public static <T> WVPResult<T> fail(int code, String msg) {
        return new WVPResult<>(code, msg, null);
    }

    public static <T> WVPResult<T> fail(ErrorCode errorCode) {
        return fail(errorCode.getCode(), errorCode.getMsg());
    }

    public static <T> WVPResult<T> fail(ErrorCode errorCode, String msg) {
        return new WVPResult<>(errorCode.getCode(), msg, null);
    }
}
