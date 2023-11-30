package com.mz.network.client.message;

import lombok.Data;

import java.util.Map;
import java.util.Optional;


@Data
public class BaseMessage {
    private String clientId;
    private String productID;
    private String deviceSN;
    private String method;
    private String token;
    private int code;
    private Map<String, Object> params;

    public Optional<Object> getParams(String key) {
        return Optional.ofNullable(params == null ? null : params.get(key));
    }

    public String getStringData(String key) {
        return getParams(key).map(String::valueOf).orElse(null);
    }

}
