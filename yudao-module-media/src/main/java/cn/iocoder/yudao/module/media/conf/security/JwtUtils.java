package cn.iocoder.yudao.module.media.conf.security;

import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import jakarta.servlet.http.HttpServletRequest;

public class JwtUtils {
    public static final String HEADER = "Authorization";

    public static Object getHeaderUser(HttpServletRequest request) {
        return SecurityFrameworkUtils.getLoginUser();
    }

    public static Long getUserId(HttpServletRequest request) {
        return SecurityFrameworkUtils.getLoginUserId();
    }
}
