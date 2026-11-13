package cn.iocoder.yudao.module.media.conf;

import cn.iocoder.yudao.module.media.conf.exception.ControllerException;
import cn.iocoder.yudao.module.media.vmanager.bean.ErrorCode;
import cn.iocoder.yudao.module.media.vmanager.bean.MzResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * 全局异常处理
 */
@Slf4j
@RestControllerAdvice(basePackages = "cn.iocoder.yudao.module.media")
public class MediaGlobalExceptionHandler {

    /**
     * 业务异常处理 (ruoyi-vue-pro 标准)
     */
    @ExceptionHandler(cn.iocoder.yudao.framework.common.exception.ServiceException.class)
    public cn.iocoder.yudao.framework.common.pojo.CommonResult<?> serviceExceptionHandler(cn.iocoder.yudao.framework.common.exception.ServiceException e) {
        log.info("[业务异常] 错误码: {}, 信息: {}", e.getCode(), e.getMessage());
        return cn.iocoder.yudao.framework.common.pojo.CommonResult.error(e.getCode(), e.getMessage());
    }

    /**
     * 默认异常处理
     * @param e 异常
     * @return 统一返回结果
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public MzResult<String> exceptionHandler(Exception e) {
        log.error("[全局异常]： ", e);
        return MzResult.fail(ErrorCode.ERROR500.getCode(), e.getMessage());
    }

    /**
     * 默认异常处理
     * @param e 异常
     * @return 统一返回结果
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public MzResult<String> exceptionHandler(MaxUploadSizeExceededException e) {
        return MzResult.fail(ErrorCode.ERROR403.getCode(), "文件过大");
    }

    /**
     * 默认异常处理
     * @param e 异常
     * @return 统一返回结果
     */
    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public MzResult<String> exceptionHandler(NoResourceFoundException e) {
        return MzResult.fail(ErrorCode.ERROR404);
    }

    /**
     * 默认异常处理
     * @param e 异常
     * @return 统一返回结果
     */
    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public MzResult<String> exceptionHandler(IllegalStateException e) {
        return MzResult.fail(ErrorCode.ERROR400);
    }

    /**
     * 默认异常处理
     * @param e 异常
     * @return 统一返回结果
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public MzResult<String> exceptionHandler(HttpRequestMethodNotSupportedException e) {
        return MzResult.fail(ErrorCode.ERROR400);
    }
    /**
     * 断言异常处理
     * @param e 异常
     * @return 统一返回结果
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.OK)
    public MzResult<String> exceptionHandler(IllegalArgumentException e) {
        return MzResult.fail(ErrorCode.ERROR100.getCode(), e.getMessage());
    }


    /**
     * 自定义异常处理， 处理controller中返回的错误
     * @param e 异常
     * @return 统一返回结果
     */
    @ExceptionHandler(ControllerException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MzResult<String>> exceptionHandler(ControllerException e) {
        return new ResponseEntity<>(MzResult.fail(e.getCode(), e.getMsg()), HttpStatus.OK);
    }

    /**
     * 登陆失败
     * @param e 异常
     * @return 统一返回结果
     */
    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<MzResult<String>> exceptionHandler(BadCredentialsException e) {
        return new ResponseEntity<>(MzResult.fail(ErrorCode.ERROR100.getCode(), "认证失败"), HttpStatus.OK);
    }
}
