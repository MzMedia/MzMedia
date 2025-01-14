package org.dromara.network.core;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hswebframework.web.dict.EnumDict;


@Getter
@AllArgsConstructor
public enum ErrorCode implements EnumDict<String> {
    unregistered("设备未注册"),
    timeout("超时"),
    unknown("未知错误"),

    ;

    private String text;

    @Override
    public String getValue() {
        return name();
    }
}
