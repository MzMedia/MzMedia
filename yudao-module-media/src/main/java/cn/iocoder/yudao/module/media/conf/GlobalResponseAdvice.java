package cn.iocoder.yudao.module.media.conf;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import com.alibaba.fastjson2.JSON;
import cn.iocoder.yudao.module.media.vmanager.bean.ErrorCode;
import cn.iocoder.yudao.module.media.vmanager.bean.MzResult;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.LinkedHashMap;

/**
 * 全局统一返回结果（仅作用于 media 模块且返回非 CommonResult 的接口）
 * @author lin
 */
@RestControllerAdvice(basePackages = "cn.iocoder.yudao.module.media")
public class GlobalResponseAdvice implements ResponseBodyAdvice<Object> {


    @Override
    public boolean supports(@NotNull MethodParameter returnType, @NotNull Class<? extends HttpMessageConverter<?>> converterType) {
        if (returnType.getMethod() == null) {
            return false;
        }
        // 如果 Controller 方法直接返回 CommonResult，不进行包装，由框架的 GlobalResponseBodyHandler 处理
        if (CommonResult.class.isAssignableFrom(returnType.getMethod().getReturnType())) {
            return false;
        }
        return true;
    }


    @Override
    public Object beforeBodyWrite(Object body, @NotNull MethodParameter returnType, @NotNull MediaType selectedContentType, @NotNull Class<? extends HttpMessageConverter<?>> selectedConverterType, @NotNull ServerHttpRequest request, @NotNull ServerHttpResponse response) {
        // 如果已经是 CommonResult，不做包装
        if (body instanceof CommonResult) {
            return body;
        }

        // 排除api文档的接口，以及标准管理端接口等不需要统一为 MzResult 的接口
        String path = request.getURI().getPath();
        String[] excludePath = {"/v3/api-docs", "/api/v1", "/index/hook", "/api/video-", "/video/webhook", "/admin-api/video"};
        for (String p : excludePath) {
            if (path.startsWith(p)) {
                return body;
            }
        }

        if (selectedContentType.equals(MediaType.parseMediaType("application/x-protobuf"))) {
            return body;
        }

        if (body instanceof MzResult) {
            return body;
        }

        if (body instanceof ErrorCode) {
            ErrorCode errorCode = (ErrorCode) body;
            return new MzResult<>(errorCode.getCode(), errorCode.getMsg(), null);
        }

        if (body instanceof String) {
            return JSON.toJSONString(MzResult.success(body));
        }

        if (body instanceof LinkedHashMap) {
            LinkedHashMap<String, Object> bodyMap = (LinkedHashMap<String, Object>) body;
            if (bodyMap.get("status") != null && (Integer)bodyMap.get("status") != 200) {
                return body;
            }
        }

        return MzResult.success(body);
    }

    /**
     * 防止返回string时出错
     * @return
     */
    /*@Bean
    public HttpMessageConverters custHttpMessageConverter() {
        return new HttpMessageConverters(new FastJsonHttpMessageConverter());
    }*/
}
