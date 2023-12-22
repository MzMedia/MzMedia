package com.mz.protocol;

import com.mz.common.core.enums.ErrCode;
import com.mz.common.core.exception.JarException;
import com.mz.common.core.utils.ComponentClassLoader;
import com.mz.protocol.config.JarPathConfig;
import com.mz.protocol.config.ProtocolConfig;
import com.mz.protocol.core.*;
import com.mz.protocol.domain.Protocol;
import com.mz.protocol.domain.ProtocolCodec;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class ProductProtocolManager {
    private final Map<Long, IProductProtocol> components = new HashMap<>();
    private final Map<String, Boolean> states = new HashMap<>();

    private IScriptEngine scriptEngine;

    @Autowired
    private JarPathConfig jarPathConfig;

    @PostConstruct
    public void init() {

    }
    public void register(Protocol component) {
        Long id = component.getId();
        if (components.containsKey(id)) {
            return;
        }

        Path path = jarPathConfig.getComponentFilePath(id.toString());
        File file = path.resolve(component.getJarFile()).toAbsolutePath().toFile();
        IProductProtocol componentInstance;
        try {
            componentInstance = ComponentClassLoader.getComponent(component.getId().toString(), file);
        } catch (Throwable e) {
            throw new JarException(ErrCode.GET_COMPONENT_INSTANCE_ERROR, e);
        }
        componentInstance.create(new ProtocolConfig(300, component.getConfig()));

        try {
            //编解码处理函数来自jar
            if(Protocol.CODEC_TYPE_CUSTOM.equals(component.getCodecType())){
                ICodec converterInstance;
                try {
                    converterInstance=ComponentClassLoader.getCodec(component.getId().toString());
                } catch (Throwable e) {
                    throw new JarException(ErrCode.GET_SPI_CONVERT_ERROR, e);
                }
                componentInstance.setCodec(converterInstance);
            } else {
                //编解码处理函数动态处理，通过脚本处理
                setScriptConvert(component, componentInstance);
            }
            //加载默认脚本
            scriptEngine = ScriptEngineFactory.getScriptEngine(component.getScriptLang());
            String componentScript = FileUtils.readFileToString(path.
                resolve(Protocol.SCRIPT_FILE_NAME).toFile(), StandardCharsets.UTF_8);
            componentInstance.setScript(componentScript);

            register(id, componentInstance);
        } catch (IOException e) {
            throw new JarException(ErrCode.GET_COMPONENT_SCRIPT_ERROR, e);
        }
    }




    public void register(Long id, IProductProtocol protocol) {
        components.put(id, protocol);
        states.put(protocol.getId(), false);
    }

    public void unregister(Long id) {
        IProductProtocol component = components.remove(id);
        states.remove(id);
        if (component == null) {
            return;
        }
        component.stop();
        component.destroy();
    }

    private void setScriptConvert(Protocol component, IProductProtocol componentInstance) throws IOException {
        //todo 获取数据库配置
        ProtocolCodec protocolCodec = new ProtocolCodec();

        ICodec scriptCodec = ScriptCodecFactory.getCovert(protocolCodec.getScriptLang());
        // 从文件方式内容
        Path converterPath = jarPathConfig.getCodecFilePath(component.getId().toString());
        String converterScript = FileUtils.readFileToString(converterPath.
            resolve(ProtocolCodec.SCRIPT_FILE_NAME).toFile(), StandardCharsets.UTF_8);

        scriptCodec.setScript(converterScript);
        scriptCodec.putScriptEnv("component", componentInstance);
        componentInstance.setCodec(scriptCodec);
    }

    public void start(String id) {
        IProductProtocol component = components.get(id);
        if (component == null) {
            return;
        }

        ProductMessageHandler messageHandler = new ProductMessageHandler(
            this, component,
            scriptEngine,
            component.getScript(), component.getCodec());

        component.setHandler(messageHandler);
        component.start();
        states.put(id, true);
    }

    public void stop(String id) {
        IProductProtocol component = components.get(id);
        if (component == null) {
            return;
        }
        component.stop();
        states.put(id, false);
    }

    public boolean isRunning(String id) {
        return states.containsKey(id) && states.get(id);
    }
}
