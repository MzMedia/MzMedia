package cn.iocoder.yudao.module.media.gb28181.bean;

import cn.iocoder.yudao.module.media.media.event.hook.HookData;
import cn.iocoder.yudao.module.media.service.bean.SSRCInfo;
import lombok.Data;

@Data
public class OpenRTPServerResult {

    private SSRCInfo ssrcInfo;
    private HookData hookData;
}
