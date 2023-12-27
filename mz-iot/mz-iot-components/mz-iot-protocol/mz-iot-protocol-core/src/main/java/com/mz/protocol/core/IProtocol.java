package com.mz.protocol.core;

import com.mz.protocol.config.ProtocolConfig;

public interface IProtocol {
    String getId();

    void create(ProtocolConfig config);

    void start();

    void stop() throws Exception;

    void destroy();

    ProtocolConfig getConfig();

    void setScript(String script);

    String getScript();

    void setScriptLang(String scriptLang);

    String getScriptLang();

    void putScriptEnv(String key, Object value);
}
