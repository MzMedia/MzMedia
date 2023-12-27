package com.mz.protocol.core;

import java.util.Map;

public interface IMessageHandler {
    void onAuth(String type, String id, Map<String, Object> params);
    void onDeviceStateChange(String id, String state, Map<String, Object> params);
    void onReceiveEvent(String id, String event, Map<String, Object> params);
    void onReceiveProperty(String id, String property, Map<String, Object> params);
    void onReceiveInfo(String id, Map<String, Object> params);

    void onReceive(Map<String, Object> params, String type, String payload);

    void send(String id, String method, Map<String, Object> params);
}
