package cn.iocoder.yudao.module.media.gb28181.controller.bean;

import lombok.Data;

import java.util.List;

@Data
public class ChannelToGroupByGbDeviceParam {
    private List<Integer> deviceIds;
    private String parentId;
    private String businessGroup;
}
