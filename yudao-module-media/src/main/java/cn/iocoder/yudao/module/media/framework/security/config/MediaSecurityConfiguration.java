package cn.iocoder.yudao.module.media.framework.security.config;

import cn.iocoder.yudao.framework.security.config.AuthorizeRequestsCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;

/**
 * 视频监控模块的安全配置
 * 放行 ZLMediaKit Webhook 回调接口和对外播放测试接口
 */
@Configuration("mediaSecurityConfiguration")
public class MediaSecurityConfiguration {

    @Bean("mediaAuthorizeRequestsCustomizer")
    public AuthorizeRequestsCustomizer mediaAuthorizeRequestsCustomizer() {
        return new AuthorizeRequestsCustomizer() {
            @Override
            public void customize(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry) {
                // 放行 ZLMediaKit Webhook 接口 (由 ZLM secret 进行认证校验)
                registry.requestMatchers("/index/hook/**").permitAll();
                registry.requestMatchers("/admin-api/media/hook/**").permitAll();
            }
        };
    }

}
