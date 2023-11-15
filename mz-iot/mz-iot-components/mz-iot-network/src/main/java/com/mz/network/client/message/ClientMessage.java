package com.mz.network.client.message;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.Optional;


@Getter
@Setter
public class ClientMessage {
    private long messageId;

    private int code;

    private String clientId;

    private Map<String, Object> data;

    public Optional<Object> getData(String key) {
        return Optional.ofNullable(data == null ? null : data.get(key));
    }

    public String getStringData(String key) {
        return getData(key).map(String::valueOf).orElse(null);
    }

}
