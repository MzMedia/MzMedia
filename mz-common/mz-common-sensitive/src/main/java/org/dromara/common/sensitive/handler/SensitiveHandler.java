package org.dromara.common.sensitive.handler;

import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import org.dromara.common.core.utils.SpringUtils;
import org.dromara.common.sensitive.annotation.Sensitive;
import org.dromara.common.sensitive.core.SensitiveService;
import org.dromara.common.sensitive.core.SensitiveStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;

import java.io.IOException;
import java.util.Objects;

/**
 * 数据脱敏json序列化工具
 *
 * @author Yjoioooo
 */
@Slf4j
public class SensitiveHandler extends JsonSerializer<String> implements ContextualSerializer {

    private SensitiveStrategy strategy;
    private String roleKey;
    private String perms;

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        try {
            SensitiveService sensitiveService = SpringUtils.getBean(SensitiveService.class);
            if (ObjectUtil.isNotNull(sensitiveService) && sensitiveService.isSensitive(roleKey, perms)) {
                gen.writeString(strategy.desensitizer().apply(value));
            } else {
                gen.writeString(value);
            }
        } catch (BeansException e) {
            log.error("脱敏实现不存在, 采用默认处理 => {}", e.getMessage());
            gen.writeString(value);
        }
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
        Sensitive annotation = property.getAnnotation(Sensitive.class);
        if (Objects.nonNull(annotation) && Objects.equals(String.class, property.getType().getRawClass())) {
            this.strategy = annotation.strategy();
            this.roleKey = annotation.roleKey();
            this.perms = annotation.perms();
            return this;
        }
        return prov.findValueSerializer(property.getType(), property);
    }
}
