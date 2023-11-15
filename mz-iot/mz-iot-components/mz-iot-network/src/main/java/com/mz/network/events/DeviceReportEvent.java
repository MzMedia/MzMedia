package com.mz.network.events;

import com.mz.network.client.message.ClientMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;



@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeviceReportEvent extends ClientMessage {

    @NotBlank
    private String action;


}
