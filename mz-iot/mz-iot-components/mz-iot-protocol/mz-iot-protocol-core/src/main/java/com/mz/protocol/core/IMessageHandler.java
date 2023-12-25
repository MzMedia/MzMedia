package com.mz.protocol.core;

import java.util.Map;

public interface IMessageHandler {
    void onReceive(Map<String, Object> params, String method, String msg);

}
