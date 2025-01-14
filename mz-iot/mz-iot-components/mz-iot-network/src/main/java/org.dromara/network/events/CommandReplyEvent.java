package org.dromara.network.events;

import org.dromara.network.client.message.BaseMessage;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;



@Getter
@Setter
@ToString
public class CommandReplyEvent extends BaseMessage {
    private String operation;
}
