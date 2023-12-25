package com.mz.protocol.core;

public interface ICodec {
    void setScript(String script);

    void decode(String msg);

    void encode(String service, String device);

    void putScriptEnv(String key, Object value);
}
