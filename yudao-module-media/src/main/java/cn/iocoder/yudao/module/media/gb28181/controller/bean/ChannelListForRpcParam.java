package cn.iocoder.yudao.module.media.gb28181.controller.bean;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChannelListForRpcParam {
    private List<Integer> channelIds;
    private Integer platformId;

}
