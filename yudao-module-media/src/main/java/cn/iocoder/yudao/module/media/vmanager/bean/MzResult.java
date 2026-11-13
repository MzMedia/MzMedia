package cn.iocoder.yudao.module.media.vmanager.bean;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * 视频模块统一返回结果封装类
 * <p>
 * 继承并深度对齐 ruoyi-vue-pro 的 {@link CommonResult}。
 *
 * @param <T> 响应数据类型
 */
@Setter
@Getter
@Schema(description = "视频模块统一返回结果")
public class MzResult<T> extends CommonResult<T> implements Cloneable {

    public MzResult() {
        super();
    }

    public MzResult(int code, String msg, T data) {
        super.setCode(code);
        super.setMsg(msg);
        super.setData(data);
    }

    public static <T> MzResult<T> success(T t, String msg) {
        return new MzResult<>(ErrorCode.SUCCESS.getCode(), msg, t);
    }

    public static <T> MzResult<T> success() {
        return new MzResult<>(ErrorCode.SUCCESS.getCode(), ErrorCode.SUCCESS.getMsg(), null);
    }

    public static <T> MzResult<T> success(T t) {
        return success(t, ErrorCode.SUCCESS.getMsg());
    }

    public static <T> MzResult<T> fail(int code, String msg) {
        return new MzResult<>(code, msg, null);
    }

    public static <T> MzResult<T> fail(ErrorCode errorCode) {
        return fail(errorCode.getCode(), errorCode.getMsg());
    }

    public static <T> MzResult<T> fail(ErrorCode errorCode, String msg) {
        return new MzResult<>(errorCode.getCode(), msg, null);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
