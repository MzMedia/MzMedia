package org.dromara.protocol.core;

import org.dromara.protocol.config.ProtocolConfig;
import lombok.Data;

import java.util.UUID;

@Data
public abstract class AbstractProductProtocol implements IProductProtocol {

    protected IMessageHandler handler;

    protected ICodec codec;

    protected ProtocolConfig config;

    protected String script;
    protected String scriptLang;

    protected String codecType;

    protected String id;

    @Override
    public void create(ProtocolConfig config) {
        this.config = config;
        this.id = UUID.randomUUID().toString();
    }
    @Override
    public ProtocolConfig getConfig() {
        return config;
    }

    @Override
    public void setScript(String script) {
        this.script = script;
    }

    @Override
    public String getScript() {
        return this.script;
    }

    @Override
    public void setScriptLang(String scriptLang) {
        this.scriptLang = scriptLang;
    }

    @Override
    public String getScriptLang() {
        return this.scriptLang;
    }

    @Override
    public String getCodecType() {
        return this.codecType;
    }

    @Override
    public void setCodecType(String codecType) {
        this.codecType = codecType;
    }

    @Override
    public void putScriptEnv(String key, Object value) {
    }
}
