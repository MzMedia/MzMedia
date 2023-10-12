package com.mz.common.social.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Social 配置属性
 *
 * @author thiszhc
 */
@Data
@Component
@ConfigurationProperties(prefix = "justauth")
public class SocialProperties {

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 授权类型
     */
    private Map<String, SocialLoginConfigProperties> type;

}
