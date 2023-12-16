package com.mz.network.client.message;

import lombok.Data;

import java.util.Map;
import java.util.Optional;

@Data
public class Route {
    private boolean isall;
    private String direction;
    private String route;
    private Map<String, Object> params;
    public Optional<Object> getParams(String key) {
        return Optional.ofNullable(params == null ? null : params.get(key));
    }

    public void setParams(String key, Object value) {
        if (params == null) {
            params = new java.util.HashMap<>();
        }
        params.put(key, value);
    }

    public String getStringData(String key) {
        return getParams(key).map(String::valueOf).orElse(null);
    }
}
