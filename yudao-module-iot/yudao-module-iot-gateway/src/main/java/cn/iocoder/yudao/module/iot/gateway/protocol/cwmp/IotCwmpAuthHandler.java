package cn.iocoder.yudao.module.iot.gateway.protocol.cwmp;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.StrUtil;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

/**
 * TR-069 CPE 接入 HTTP 认证处理器
 *
 * @author 芋道源码
 */
@Slf4j
@RequiredArgsConstructor
public class IotCwmpAuthHandler implements Handler<RoutingContext> {

    private final IotCwmpConfig config;

    @Override
    public void handle(RoutingContext ctx) {
        if (config == null || Boolean.FALSE.equals(config.getAuthEnabled())) {
            ctx.next();
            return;
        }

        String authHeader = ctx.request().getHeader("Authorization");
        if (StrUtil.isBlank(authHeader)) {
            sendUnauthorized(ctx);
            return;
        }

        if (authHeader.startsWith("Basic ")) {
            String token = authHeader.substring(6).trim();
            try {
                String decoded = Base64.decodeStr(token, StandardCharsets.UTF_8);
                String[] parts = decoded.split(":", 2);
                if (parts.length == 2) {
                    String username = parts[0];
                    String password = parts[1];
                    // 校验是否匹配默认配置凭证
                    if (config.getDefaultUsername().equals(username) && config.getDefaultPassword().equals(password)) {
                        ctx.next();
                        return;
                    }
                }
            } catch (Exception e) {
                log.warn("[handle][解析 Basic Auth 异常: {}]", e.getMessage());
            }
        }

        // 认证未通过
        sendUnauthorized(ctx);
    }

    private void sendUnauthorized(RoutingContext ctx) {
        ctx.response()
                .setStatusCode(401)
                .putHeader("WWW-Authenticate", "Basic realm=\"MzMedia-ACS\"")
                .end("Unauthorized");
    }

}
