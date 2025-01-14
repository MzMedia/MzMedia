package org.dromara.protocol.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
@Data
public class JarPathConfig {
    @Value("${component.dir:./data/components}")
    private String componentDir;

    @Value("${converter.dir:./data/codecs}")
    private String codecDir;

    public Path getComponentFilePath(String comId) {
        return Paths.get(componentDir, comId)
            .toAbsolutePath().normalize();
    }

    public  Path getCodecFilePath(String conId) {
        return Paths.get(codecDir, conId)
            .toAbsolutePath().normalize();
    }
}
