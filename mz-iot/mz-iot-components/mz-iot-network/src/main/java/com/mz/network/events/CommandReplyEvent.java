package com.mz.network.events;

import com.mz.network.client.message.ClientMessage;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;



@Getter
@Setter
@ToString
public class CommandReplyEvent extends ClientMessage {
    private String operation;
}
