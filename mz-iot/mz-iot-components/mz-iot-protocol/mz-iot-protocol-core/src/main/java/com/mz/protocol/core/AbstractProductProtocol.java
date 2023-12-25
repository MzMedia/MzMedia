package com.mz.protocol.core;

import com.mz.protocol.config.ProtocolConfig;
import lombok.Data;

import java.util.UUID;

@Data
public abstract class AbstractProductProtocol implements IProductProtocol {

    protected IMessageHandler handler;

    protected ICodec codec;

    protected ProtocolConfig config;

    protected String script;

    protected String id;

    @Override
    public void create(ProtocolConfig config) {
        this.config = config;
        this.id = UUID.randomUUID().toString();
    }

    @Override
    public void setScript(String script) {
        this.script = script;
    }

    @Override
    public String getScript() {
        return script;
    }
    @Override
    public ProtocolConfig getConfig() {
        return config;
    }

    @Override
    public void putScriptEnv(String key, Object value) {
    }
}
