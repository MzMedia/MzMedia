package org.dromara.network.events;

import com.alibaba.fastjson.JSON;
import org.dromara.network.core.IotCommand;
import lombok.*;



@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CommandSendEvent {
    private String topic;

    private String clientId;

    private IotCommand command;

    public String toJSONString() {
        return JSON.toJSONString(this);
    }

    public String toString() {
        return toJSONString();
    }
}
