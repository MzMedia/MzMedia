package org.dromara.common.core.exception;

import org.dromara.common.core.enums.ErrCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProtocolException extends RuntimeException{
    /**
     * 所属模块
     */
    private String module;

    /**
     * 错误码
     */
    private Integer code;

    /**
     * 错误消息
     */
    private String message;

    public ProtocolException(String message) {
        super(message);
        this.message = message;
        this.code = ErrCode.SYSTEM_EXCEPTION.getKey();
    }

    /**
     * 统一异常消息处理
     *
     * @param errCode 异常枚举值
     */
    public ProtocolException(ErrCode errCode) {
        this.message = errCode.getValue();
        this.code = errCode.getKey();
    }

    public ProtocolException(ErrCode errCode, Throwable cause) {
        super(cause);
        this.message = errCode.getValue();
    }

    public ProtocolException(ErrCode errCode, String message) {
        this.message = message;
        this.code = errCode.getKey();
    }

    public ProtocolException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProtocolException(Throwable cause) {
        super(cause);
    }
}
