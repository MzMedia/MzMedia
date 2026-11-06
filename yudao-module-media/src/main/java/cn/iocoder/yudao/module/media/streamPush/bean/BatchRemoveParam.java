package cn.iocoder.yudao.module.media.streamPush.bean;

import lombok.Data;

import java.util.Set;

@Data
public class BatchRemoveParam {
    private Set<Integer> ids;
}
