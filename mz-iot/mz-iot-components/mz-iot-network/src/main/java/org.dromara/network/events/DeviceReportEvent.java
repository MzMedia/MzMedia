package org.dromara.network.events;

import org.dromara.network.client.message.BaseMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;



@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeviceReportEvent extends BaseMessage {

    @NotBlank
    private String action;
}
